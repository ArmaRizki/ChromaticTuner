package com.armarizki.chromatic.controller.tuner

import kotlin.math.abs
import kotlin.math.roundToInt
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.AMDF
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import com.armarizki.chromatic.controller.tuner.Tuner.Companion.HIGHEST_NOTE
import com.armarizki.chromatic.controller.tuner.Tuner.Companion.LOWEST_NOTE
import com.armarizki.chromatic.model.error.TunerException
import com.armarizki.chromatic.view.PermissionHandler
import com.armarizki.music.Notes
import com.armarizki.music.Tuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Tuner(
    tuning: Tuning = Tuning.STANDARD
) {

    companion object {

        /** Threshold in semitones that note offset must be below to be considered in tune. */
        const val TUNED_OFFSET_THRESHOLD = 0.05

        /** Time in ms that a note must be held below the threshold for before being considered in tune. */
        const val TUNED_SUSTAIN_TIME = 900

        // Audio Dispatcher Constants
        /** Microphone sample rate. */
        private const val SAMPLE_RATE = 44100

        /** Audio buffer size. */
        private const val AUDIO_BUFFER_SIZE = 2048

        /** Index of the lowest detectable note. */
        val LOWEST_NOTE = Notes.getIndex("D1")

        /** Index of the highest detectable note. */
        val HIGHEST_NOTE = Notes.getIndex("B4")
    }

    /** Mutable backing property for [tuning]. */
    private val _tuning = MutableStateFlow(tuning)

    /** Guitar tuning used for comparison. */
    val tuning = _tuning.asStateFlow()

    /** Mutable backing property for [selectedString]. */
    private val _selectedString = MutableStateFlow(0)

    /** Index of the currently selected string within the tuning. */
    val selectedString = _selectedString.asStateFlow()

    /** Mutable backing property for [selectedNote]. */
    private val _selectedNote = MutableStateFlow(Notes.getIndex("E2"))

    /** The index of the currently selected note. */
    val selectedNote = _selectedNote.asStateFlow()

    /** Mutable backing property for [noteOffset] */
    private val _noteOffset = MutableStateFlow<Double?>(null)

    /** The offset between the currently playing note and the selected string. */
    val noteOffset = _noteOffset.asStateFlow()

    /** Mutable backing property for [autoDetect]. */
    private val _autoDetect = MutableStateFlow(true)

    /** Whether the tuner will automatically detect the currently playing string. */
    val autoDetect = _autoDetect.asStateFlow()

    /** Mutable backing property for [tuned]. */
    private val _tuned = MutableStateFlow(BooleanArray(tuning.numStrings()) { false })

    /** Whether each string has been tuned. */
    val tuned = _tuned.asStateFlow()

    /** Mutable backing property for [noteTuned]. */
    private val _noteTuned = MutableStateFlow(false)

    /** Whether the currently playing chromatic note is tuned. */
    val noteTuned = _noteTuned.asStateFlow()

    /** Mutable backing property for [chromatic]. */
    private val _chromatic = MutableStateFlow(false)

    /** Whether the tuner is currently in chromatic mode, or instrument mode. */
    val chromatic = _chromatic.asStateFlow()

    /** Whether the tuner is currently running. */
    private var running = false

    /** Audio dispatcher used to receive incoming audio data.  */
    private var dispatcher: AudioDispatcher? = null

    /** Mutable backing property for [error]. */
    private val _error = MutableStateFlow<Exception?>(null)

    /** Reference pitch for A4 (Hz). */
    private val _a4Pitch = MutableStateFlow(440.0)
    val a4Pitch = _a4Pitch.asStateFlow()

    fun setA4Pitch(value: Double) {
        _a4Pitch.update { value }
    }


    /** Error preventing the tuner from running. `null` if no error has occurred. */
    val error = _error.asStateFlow()

    /** Selects the [nth][n] string in the tuning for comparison. */
    fun selectString(n: Int) {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }
        _selectedString.update { n }
        _autoDetect.update { false }
    }

    /** Sets the guitar tuning for comparison. */
    fun setTuning(tuning: Tuning) {
        if (chromatic.value) {
            setChromatic(false)
        }
        _tuning.update {
            updateTunedStatus(it, tuning)
            tuning
        }
        if (selectedString.value >= tuning.numStrings()) {
            _selectedString.update { tuning.numStrings() - 1 }
        }
    }

    /** Tunes all strings in the tuning up by one semitone */
    fun tuneUp(): Boolean {
        return if (tuning.value.max().rootNoteIndex < HIGHEST_NOTE) {
            _tuning.update { it.higherTuning() }
            _tuned.update { BooleanArray(tuning.value.numStrings()) { false } }
            true
        } else false
    }

    /** Tunes all strings in the tuning down by one semitone */
    fun tuneDown(): Boolean {
        return if (tuning.value.min().rootNoteIndex > LOWEST_NOTE) {
            _tuning.update { it.lowerTuning() }
            _tuned.update { BooleanArray(tuning.value.numStrings()) { false } }
            true
        } else false
    }

    /** Tunes the [nth][n] string in the tuning up by one semitone.
     * @return False if the string could not be tuned any lower, true otherwise.
     */
    fun tuneStringUp(n: Int): Boolean {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }

        return if (tuning.value.getString(n).rootNoteIndex < HIGHEST_NOTE) {
            _tuning.update { tuning ->
                tuning.withString(n, tuning.getString(n).higherString())
            }
            setTuned(n, false)
            true
        } else false
    }

    /**
     * Tunes the [nth][n] string in the tuning down by one semitone.
     * @return False if the string could not be tuned any lower, true otherwise.
     */
    fun tuneStringDown(n: Int): Boolean {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }

        return if (tuning.value.getString(n).rootNoteIndex > LOWEST_NOTE) {
            _tuning.update { tuning ->
                tuning.withString(n, tuning.getString(n).lowerString())
            }
            setTuned(n, false)
            true
        } else false
    }

    /**
     * Selects the note to tune to in chromatic mode.
     * @param noteIndex The index of the note to select, must be between [LOWEST_NOTE] and [HIGHEST_NOTE].
     */
    fun selectNote(noteIndex: Int) {
        require(noteIndex in LOWEST_NOTE..HIGHEST_NOTE) { "Invalid note index." }
        if (selectedNote.value != noteIndex) {
            _noteTuned.update { false }
        }
        _selectedNote.update { noteIndex }
        _autoDetect.update { false }
    }

    /**
     * Sets the [tuned] value of the [nth][n] string.
     * If in chromatic mode, sets the [noteTuned] value instead.
     */
    fun setTuned(n: Int = selectedString.value, tuned: Boolean = true) {
        require(n in 0 until tuning.value.numStrings()) { "Invalid string index." }
        if (chromatic.value) {
            _noteTuned.update { tuned }
        } else {
            _tuned.update { old -> old.clone().also { it[n] = tuned } }
        }
    }

    /** Sets whether the tuner will automatically detect the currently playing string. */
    fun setAutoDetect(on: Boolean) {
        _autoDetect.update { on }
    }

    /** Sets whether the tuner is in chromatic mode or instrument mode. */
    fun setChromatic(on: Boolean = true) {
        // Reset tuned state.
        _tuned.update { BooleanArray(tuning.value.numStrings()) { false } }
        _noteTuned.update { false }

        // Set chromatic or instrument mode
        _chromatic.update { on }
    }

    private fun updateTunedStatus(oldTuning: Tuning, newTuning: Tuning) {
        _tuned.update {
            if (oldTuning.numStrings() != newTuning.numStrings()) {
                BooleanArray(newTuning.numStrings()) { false }
            } else {
                it.clone().also { newArr ->
                    for (i in 0 until newTuning.numStrings()) {
                        if (newTuning.getString(i) != oldTuning.getString(i)) {
                            newArr[i] = false
                        }
                    }
                }
            }
        }
    }

    fun processPitch(result: PitchDetectionResult) {
        if (result.isPitched) {
            val a4 = _a4Pitch.value
            val notePlaying = Notes.getOffsetFromA4(result.pitch.toDouble(), a4)

            if (autoDetect.value) {
                if (chromatic.value) {
                    val closestNote = notePlaying.roundToInt()
                    if (selectedNote.value != closestNote) {
                        _noteTuned.update { false }
                    }
                    _selectedNote.update { closestNote }
                } else {
                    _selectedString.update {
                        tuning.value.getStringNum(
                            tuning.value.minBy { abs(it.getNoteIndex(0) - notePlaying) }
                        )
                    }
                }

            }

            // Update note offset.
            _noteOffset.update { calcNoteOffset(notePlaying) }
        } else {
            _noteOffset.update { null }
        }
    }

    /** Returns the offset between the specified [note][notePlaying] and the root note of the selected string. */
    private fun calcNoteOffset(notePlaying: Double): Double {
        val noteIndex = if (chromatic.value) {
            selectedNote.value
        } else {
            val str = tuning.value.getString(selectedString.value)
            str.getNoteIndex(0)
        }
        return notePlaying - noteIndex
    }

    @Throws(IllegalStateException::class, TunerException::class)
    fun start(ph: PermissionHandler) {
        check(!running) { "Tuner already started." }
        check(ph.check()) { "RECORD_AUDIO permission not granted." }

        running = true
        _error.update { null }

        var bufferSize = AUDIO_BUFFER_SIZE
        var sampleRate = SAMPLE_RATE
        while (dispatcher == null) {
            try {
                // Create audio dispatcher from default microphone.
                dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, 0)

                // Setup and add pitch processor.
                val pdh = PitchDetectionHandler { result, _ -> processPitch(result) }
                val a4 = _a4Pitch.value
                val pitchProcessor = PitchProcessor(
                    AMDF(
                        sampleRate.toFloat(),
                        bufferSize,
                        Notes.getPitch(LOWEST_NOTE, a4),
                        Notes.getPitch(HIGHEST_NOTE, a4)
                    ),
                    pdh
                )
                dispatcher?.addAudioProcessor(pitchProcessor)

                // Start the audio dispatcher (producer) thread.
                Thread(dispatcher, "audio-dispatcher").start()
            } catch(e: Exception) {
                    // Extract the required buffer size from the exception message.
                    val requiredBufferSize = e.message?.substringAfter("should be at least ")?.substringBefore("\n")?.trim()?.toIntOrNull()

                    if (requiredBufferSize == null || requiredBufferSize == bufferSize) {
                        // If we have tried the device's required buffer size and still failed, throw an exception.
                        val err = TunerException(e.message, e)
                        _error.update { err }
                        running = false
                        throw err
                    } else {
                        bufferSize = requiredBufferSize
                        sampleRate = (SAMPLE_RATE.toFloat() * (bufferSize.toFloat() / AUDIO_BUFFER_SIZE.toFloat())).roundToInt()
                    }
            }
        }
    }

    /** Stops listening to incoming audio and note comparison. */
    fun stop() {
        if (running) {
            running = false

            // Stop the audio dispatcher.
            dispatcher!!.stop()
            dispatcher = null
        }
    }
}
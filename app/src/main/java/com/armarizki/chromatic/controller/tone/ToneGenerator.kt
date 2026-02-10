package com.armarizki.chromatic.controller.tone

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.sin
import kotlin.math.PI
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class ToneGenerator {

    private val sampleRate = 44100
    private val isPlaying = AtomicBoolean(false)
    private var audioTrack: AudioTrack? = null
    private var playThread: Thread? = null

    fun play(frequencyHz: Double, durationMs: Int = 200) {
        stop()

        isPlaying.set(true)

        playThread = thread(
            start = true,
            name = "ToneGeneratorThread"
        ) {
            try {
                val numSamples = (durationMs * sampleRate) / 1000
                val buffer = ShortArray(numSamples)

                for (i in buffer.indices) {
                    val angle = 2.0 * PI * i * frequencyHz / sampleRate
                    buffer[i] = (sin(angle) * Short.MAX_VALUE).toInt().toShort()
                }

                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(
                        maxOf(minBufferSize, buffer.size * 2)
                    )
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack?.apply {
                    write(buffer, 0, buffer.size)
                    play()

                    val sleepTime = durationMs.toLong()
                    Thread.sleep(sleepTime)

                    stop()
                    release()
                }
            } catch (_: InterruptedException) {
            } catch (_: Exception) {
            } finally {
                audioTrack = null
                isPlaying.set(false)
            }
        }
    }

    /**
     * Stop current tone immediately.
     */
    fun stop() {
        if (isPlaying.get()) {
            try {
                playThread?.interrupt()
                audioTrack?.stop()
                audioTrack?.release()
            } catch (_: Exception) {
            } finally {
                audioTrack = null
                isPlaying.set(false)
            }
        }
    }
}

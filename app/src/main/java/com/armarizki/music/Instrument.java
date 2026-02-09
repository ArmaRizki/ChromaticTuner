
package com.armarizki.music;

import org.billthefarmer.mididriver.GeneralMidiConstants;

/**
 * Enum representing an stringed instrument.
 *
 * @author Rohan Khayech
 */
public enum Instrument {

    /** Guitar */
    GUITAR("Guitar"),

    /** Bass */
    BASS("Bass", 4, GeneralMidiConstants.ELECTRIC_BASS_FINGER),

    /** Ukulele */
    UKULELE("Ukulele", 4, GeneralMidiConstants.ACOUSTIC_GUITAR_NYLON),

    /** Other Instrument */
    OTHER("Other");

    /** Name of the instrument. */
    private final String name;

    /** Default number of strings the instrument has. */
    private final int defaultNumStrings;

    /** Default value for the default number of strings for an instrument. */
    private static final int DEFAULT_NUM_STRINGS = 6;

    /** The MIDI instrument code the instrument should use when playing a note. */
    private final byte midiInstrument;

    /** Default MIDI instrument code to use when playing a note. */
    private static final byte DEFAULT_MIDI_INSTRUMENT = GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN;

    /**
     * Defines an instrument with the specified name and default values.
     * @param name The name of the instrument.
     */
    Instrument(String name) {
        this.name = name;
        this.defaultNumStrings = DEFAULT_NUM_STRINGS;
        this.midiInstrument = DEFAULT_MIDI_INSTRUMENT;
    }

    /**
     * Defines an instrument with the specified values.
     * @param name The name of the instrument.
     * @param defaultNumStrings Default number of strings the instrument has.
     * @param midiInstrument The MIDI instrument code the instrument should use when playing a note.
     */
    Instrument(String name, int defaultNumStrings, byte midiInstrument) {
        this.name = name;
        this.defaultNumStrings = defaultNumStrings;
        this.midiInstrument = midiInstrument;
    }

    /** @return The name of the instrument. */
    public String getName() {
        return name;
    }

    /** @return The default number of strings the instrument has. */
    public int getDefaultNumStrings() {
        return defaultNumStrings;
    }

    /** @return The MIDI instrument code the instrument should use when playing a note. */
    public byte getMidiInstrument() {
        return midiInstrument;
    }
}

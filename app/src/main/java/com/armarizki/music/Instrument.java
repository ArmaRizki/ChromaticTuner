
package com.armarizki.music;

import org.billthefarmer.mididriver.GeneralMidiConstants;

public enum Instrument {

    GUITAR("Guitar"),

    BASS("Bass", 4, GeneralMidiConstants.ELECTRIC_BASS_FINGER);

    private final String name;

    private final int defaultNumStrings;

    private static final int DEFAULT_NUM_STRINGS = 6;

    private final byte midiInstrument;

    private static final byte DEFAULT_MIDI_INSTRUMENT = GeneralMidiConstants.ELECTRIC_GUITAR_CLEAN;

    Instrument(String name) {
        this.name = name;
        this.defaultNumStrings = DEFAULT_NUM_STRINGS;
        this.midiInstrument = DEFAULT_MIDI_INSTRUMENT;
    }

    Instrument(String name, int defaultNumStrings, byte midiInstrument) {
        this.name = name;
        this.defaultNumStrings = defaultNumStrings;
        this.midiInstrument = midiInstrument;
    }

    public String getName() {
        return name;
    }

    public int getDefaultNumStrings() {
        return defaultNumStrings;
    }

    public byte getMidiInstrument() {
        return midiInstrument;
    }
}

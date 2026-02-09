
package com.armarizki.music;

import static org.junit.Assert.assertEquals;

import org.billthefarmer.mididriver.GeneralMidiConstants;
import org.junit.Test;

public class InstrumentTest {

    private final Instrument instrument = Instrument.BASS;

    @Test
    public void testGetName() {
        assertEquals("Bass", instrument.getName());
    }

    @Test
    public void testGetDefaultNumStrings() {
        assertEquals(4, instrument.getDefaultNumStrings());
    }

    @Test
    public void getMidiInstrument() {
        assertEquals(GeneralMidiConstants.ELECTRIC_BASS_FINGER, instrument.getMidiInstrument());
    }
}
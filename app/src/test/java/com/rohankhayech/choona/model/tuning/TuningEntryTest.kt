
package com.rohankhayech.choona.model.tuning

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TuningEntryTest {
    @Test
    fun testChromatic() {
        val chromatic = TuningEntry.ChromaticTuning
        assertEquals("Chromatic", chromatic.name)
        assertNull(chromatic.tuning)
        assertEquals("chromatic", chromatic.key)
        assertTrue(chromatic.hasName())
        assertEquals("Chromatic Tuning", chromatic.toString())
    }

    @Test
    fun testInstrument() {
        val entry = TuningEntry.InstrumentTuning(Tunings.STANDARD)
        assertEquals("Standard", entry.name)
        assertEquals(Tunings.STANDARD, entry.tuning)
        assertTrue(entry.hasName())
        assertEquals(
            "${Tunings.STANDARD.instrument}-[${Tunings.STANDARD.toFullString()}]",
            entry.key
        )
        assertEquals("Instrument Tuning: ${Tunings.STANDARD}", entry.toString())
    }

    @Test
    fun testEqualsAndHashCode() {
        val entry1 = TuningEntry.InstrumentTuning(Tunings.OPEN_G)
        val entry2 = TuningEntry.InstrumentTuning(Tunings.OPEN_G)
        val entry3 = TuningEntry.InstrumentTuning(Tunings.OPEN_D)
        val entry4: TuningEntry = TuningEntry.ChromaticTuning
        assertEquals(entry1, entry2)
        assertEquals(entry1.hashCode(), entry2.hashCode())
        assertEquals(entry4, entry4)
        assertEquals(entry4.hashCode(), entry4.hashCode())
        assertNotEquals(entry1, entry4)
        assertNotEquals(entry1.hashCode(), entry4.hashCode())
        assertNotEquals(entry1, entry3)
        assertNotEquals(entry1.hashCode(), entry3.hashCode())
    }
}

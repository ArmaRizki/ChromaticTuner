package com.armarizki.chromatic.controller.fileio

import com.armarizki.chromatic.model.tuning.TuningEntry
import com.armarizki.music.Tuning
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class TuningFileIOTest {

    companion object {
        private const val TUNINGS_JSON = "{\"tunings\":[{\"strings\":\"E4 B3 G3 D3 A2 E2\",\"name\":\"Standard\",\"instrument\":\"GUITAR\",\"category\":\"COMMON\"},{\"strings\":\"E4 B3 G3 D3 A2 D2\",\"name\":\"Drop D\",\"instrument\":\"GUITAR\",\"category\":\"COMMON\"},{\"strings\":\"G3 D3 A2 E2\",\"instrument\":\"GUITAR\"},{\"name\":\"Chromatic\",\"instrument\":\"chromatic\"}]}"
    }

    @Test
    fun testParseTunings() {
        val tunings = TuningFileIO.parseTunings(TUNINGS_JSON)
        val expected: MutableSet<TuningEntry> = LinkedHashSet()
        expected.add(TuningEntry.InstrumentTuning(Tuning.STANDARD))
        expected.add(TuningEntry.InstrumentTuning(Tuning.DROP_D))
        expected.add(TuningEntry.InstrumentTuning(Tuning.fromString("G3 D3 A2 E2")))
        expected.add(TuningEntry.ChromaticTuning)
        Assert.assertEquals(expected, tunings)
    }

    @Test
    @Throws(JSONException::class)
    fun testEncodeTunings() {
        val tunings: MutableSet<TuningEntry> = LinkedHashSet()
        tunings.add(TuningEntry.InstrumentTuning(Tuning.STANDARD))
        tunings.add(TuningEntry.InstrumentTuning(Tuning.DROP_D))
        tunings.add(TuningEntry.InstrumentTuning(Tuning.fromString("G3 D3 A2 E2")))
        tunings.add(TuningEntry.ChromaticTuning)
        val json = TuningFileIO.encodeTunings(tunings)
        Assert.assertEquals(JSONObject(TUNINGS_JSON).toString(), JSONObject(json).toString())
    }
}
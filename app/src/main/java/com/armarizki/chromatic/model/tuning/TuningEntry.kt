package com.armarizki.chromatic.model.tuning

import androidx.compose.runtime.Immutable
import com.armarizki.music.Tuning

@Immutable
sealed class TuningEntry(
     
    open val name: String?
) {
     
    abstract val tuning: Tuning?

     
    abstract val key: String

     
    fun hasName(): Boolean = !name.isNullOrEmpty()

     
    @Immutable
    object ChromaticTuning: TuningEntry("Chromatic") {
        override val tuning: Tuning? = null
        override val key: String = "chromatic"

        override fun toString(): String {
            return "Chromatic Tuning"
        }
    }

     
    @Immutable
    class InstrumentTuning(
        override val tuning: Tuning
    ): TuningEntry(if (tuning.hasName()) tuning.name else null) {
        override val key: String
            get() = "${tuning.instrument}-[${tuning.toFullString()}]"

        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other !is InstrumentTuning) return false
            return tuning == other.tuning
        }

        override fun hashCode(): Int {
             return tuning.hashCode()
        }

        override fun toString(): String {
            return "Instrument Tuning: $tuning"
        }
    }
}
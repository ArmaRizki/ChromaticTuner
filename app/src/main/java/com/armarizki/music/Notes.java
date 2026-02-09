package com.armarizki.music;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Notes {

    public static final double DEFAULT_A4_PITCH = 440.0;

    public static final int A4_MIDI_NOTE_NUMBER = 69;

    public static final List<String> NOTE_SYMBOLS = Collections.unmodifiableList(
            Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    );

    private Notes() {
    }

    public static double getPitch(int noteIndex, double a4Pitch) {
        return Math.pow(2.0, noteIndex / 12.0) * a4Pitch;
    }

    public static double getOffsetFromA4(double pitch, double a4Pitch) {
        return 12.0 * (Math.log(pitch / a4Pitch) / Math.log(2.0));
    }

    public static int getIndex(double pitch, double a4Pitch) {
        return (int) Math.round(getOffsetFromA4(pitch, a4Pitch));
    }


    public static double getPitch(int noteIndex) {
        return getPitch(noteIndex, DEFAULT_A4_PITCH);
    }

    public static double getOffsetFromA4(double pitch) {
        return getOffsetFromA4(pitch, DEFAULT_A4_PITCH);
    }

    public static int getIndex(double pitch) {
        return getIndex(pitch, DEFAULT_A4_PITCH);
    }

    public static int getIndex(String note) {
        Objects.requireNonNull(note, "Note symbol cannot be null.");

        int len = note.length();
        if (len < 2 || len > 3) {
            throw new IllegalArgumentException("Invalid note symbol.");
        }

        int offset = getOffsetWithinOctave(getRootNote(note));

        try {
            int octave = getOctave(note);
            return (octave - 4) * 12 + offset;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid note symbol.", e);
        }
    }

    public static String getSymbol(int noteIndex) {
        int octave = 4 + Math.floorDiv(noteIndex + 9, 12);
        int noteInOctave = Math.floorMod(noteIndex + 9, 12);
        return NOTE_SYMBOLS.get(noteInOctave) + octave;
    }

    public static String getRootNote(String note) {
        return note.substring(0, note.length() - 1);
    }

    public static int getOctave(String note) {
        return Integer.parseInt(note.substring(note.length() - 1));
    }

    private static int getOffsetWithinOctave(String rootNote) {
        int index = NOTE_SYMBOLS.indexOf(rootNote);
        if (index == -1) {
            throw new IllegalArgumentException("Invalid note symbol.");
        }
        return index - 9;
    }
}

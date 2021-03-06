package com.cs429.amadeus.helpers;

import com.cs429.amadeus.Note;

/**
 * This class contains static methods to convert between midi and staff notes.
 */
public class NoteCalculator {
	private static final double MAX_MIDI = 128;
	private static String[] midiTranslationArray = { "C", "C#", "D", "D#", "E",
			"F", "F#", "G", "G#", "A", "A#", "B" };

	private NoteCalculator() {
	}

	public static float getFreqFromMIDI(float midi) {
		float pow = (midi - 69) / 12.0f;
		return (float) (Math.pow(2, pow) * 440.0f);
	}

	/**
	 * Returns the staff note associated with the given midi note. By default,
	 * the returned note's type is a quarter note.
	 * 
	 * @param midi
	 *            - midi note
	 * @return - the staff note associated with the given midi note
	 */
	public static Note getNoteFromMIDI(double midi) {
		if (midi < 0 || midi > MAX_MIDI) {
			return null;
		}

		int midiInt = (int) Math.round(midi);
		int octave = midiInt / 12;
		String noteName = midiTranslationArray[midiInt % 12];
		return new Note(noteName.charAt(0), octave, noteName.length() > 1,
				Note.QUARTER_NOTE);
	}

	/**
	 * Returns the midi note associated with the given staff note.
	 * 
	 * @param note
	 *            - staff note
	 * @return - the midi note associated with the given staff note
	 */
	public static Float getMIDIFromNote(Note note) {
		if (note == null || note.octave < 0) {
			return null;
		}

		String fullNote = note.note + (note.isSharp ? "#" : "");

		int n = -1;
		for (int i = 0; i < midiTranslationArray.length; i++) {
			if (midiTranslationArray[i].equals(fullNote)) {
				n = i;
				break;
			}
		}

		if (n < 0) {
			return null;
		}

		return (note.octave * 12.0f) + n;
	}
}

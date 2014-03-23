
package com.cs429.amadeus.helpers;

import java.util.ArrayList;
import java.util.HashMap;

import com.cs429.amadeus.Note;

public class NoteCalculator
{

	private static final double MAX_MIDI = 128;
	private HashMap<Double, String> frequencyToNoteMap;
	private ArrayList<Double> noteFrequencies;

	/**
	 * Deprecated
	 */
	public NoteCalculator()
	{
		initializeFrequencyToNoteMap();
	}

	/**
	 * Deprecated
	 */
	private void initializeFrequencyToNoteMap()
	{
		frequencyToNoteMap = new HashMap<Double, String>();
		noteFrequencies = new ArrayList<Double>();

		frequencyToNoteMap.put(16.35, "C0");
		frequencyToNoteMap.put(17.32, "C#0/Db0");
		frequencyToNoteMap.put(18.35, "D0");
		frequencyToNoteMap.put(19.45, "D#0/Eb0");
		frequencyToNoteMap.put(20.6, "E0");
		frequencyToNoteMap.put(21.83, "F0");
		frequencyToNoteMap.put(23.12, "F#0/Gb0");
		frequencyToNoteMap.put(24.5, "G0");
		frequencyToNoteMap.put(25.96, "G#0/Ab0");
		frequencyToNoteMap.put(27.5, "A0");
		frequencyToNoteMap.put(29.14, "A#0/Bb0");
		frequencyToNoteMap.put(30.87, "B0");
		frequencyToNoteMap.put(32.7, "C1");
		frequencyToNoteMap.put(34.65, "C#1/Db1");
		frequencyToNoteMap.put(36.71, "D1");
		frequencyToNoteMap.put(38.89, "D#1/Eb1");
		frequencyToNoteMap.put(41.2, "E1");
		frequencyToNoteMap.put(43.65, "F1");
		frequencyToNoteMap.put(46.25, "F#1/Gb1");
		frequencyToNoteMap.put(49.0, "G1");
		frequencyToNoteMap.put(51.91, "G#1/Ab1");
		frequencyToNoteMap.put(55.0, "A1");
		frequencyToNoteMap.put(58.27, "A#1/Bb1");
		frequencyToNoteMap.put(61.74, "B1");
		frequencyToNoteMap.put(65.41, "C2");
		frequencyToNoteMap.put(69.3, "C#2/Db2");
		frequencyToNoteMap.put(73.42, "D2");
		frequencyToNoteMap.put(77.78, "D#2/Eb2");
		frequencyToNoteMap.put(82.41, "E2");
		frequencyToNoteMap.put(87.31, "F2");
		frequencyToNoteMap.put(92.5, "F#2/Gb2");
		frequencyToNoteMap.put(98.0, "G2");
		frequencyToNoteMap.put(103.83, "G#2/Ab2");
		frequencyToNoteMap.put(110.0, "A2");
		frequencyToNoteMap.put(116.54, "A#2/Bb2");
		frequencyToNoteMap.put(123.47, "B2");
		frequencyToNoteMap.put(130.81, "C3");
		frequencyToNoteMap.put(138.59, "C#3/Db3");
		frequencyToNoteMap.put(146.83, "D3");
		frequencyToNoteMap.put(155.56, "D#3/Eb3");
		frequencyToNoteMap.put(164.81, "E3");
		frequencyToNoteMap.put(174.61, "F3");
		frequencyToNoteMap.put(185.0, "F#3/Gb3");
		frequencyToNoteMap.put(196.0, "G3");
		frequencyToNoteMap.put(207.65, "G#3/Ab3");
		frequencyToNoteMap.put(220.0, "A3");
		frequencyToNoteMap.put(233.08, "A#3/Bb3");
		frequencyToNoteMap.put(246.94, "B3");
		frequencyToNoteMap.put(261.63, "C4");
		frequencyToNoteMap.put(277.18, "C#4/Db4");
		frequencyToNoteMap.put(293.66, "D4");
		frequencyToNoteMap.put(311.13, "D#4/Eb4");
		frequencyToNoteMap.put(329.63, "E4");
		frequencyToNoteMap.put(349.23, "F4");
		frequencyToNoteMap.put(369.99, "F#4/Gb4");
		frequencyToNoteMap.put(392.0, "G4");
		frequencyToNoteMap.put(415.3, "G#4/Ab4");
		frequencyToNoteMap.put(440.0, "A4");
		frequencyToNoteMap.put(466.16, "A#4/Bb4");
		frequencyToNoteMap.put(493.88, "B4");
		frequencyToNoteMap.put(523.25, "C5");
		frequencyToNoteMap.put(554.37, "C#5/Db5");
		frequencyToNoteMap.put(587.33, "D5");
		frequencyToNoteMap.put(622.25, "D#5/Eb5");
		frequencyToNoteMap.put(659.25, "E5");
		frequencyToNoteMap.put(698.46, "F5");
		frequencyToNoteMap.put(739.99, "F#5/Gb5");
		frequencyToNoteMap.put(783.99, "G5");
		frequencyToNoteMap.put(830.61, "G#5/Ab5");
		frequencyToNoteMap.put(880.0, "A5");
		frequencyToNoteMap.put(932.33, "A#5/Bb5");
		frequencyToNoteMap.put(987.77, "B5");
		frequencyToNoteMap.put(1046.5, "C6");
		frequencyToNoteMap.put(1108.73, "C#6/Db6");
		frequencyToNoteMap.put(1174.66, "D6");
		frequencyToNoteMap.put(1244.51, "D#6/Eb6");
		frequencyToNoteMap.put(1318.51, "E6");
		frequencyToNoteMap.put(1396.91, "F6");
		frequencyToNoteMap.put(1479.98, "F#6/Gb6");
		frequencyToNoteMap.put(1567.98, "G6");
		frequencyToNoteMap.put(1661.22, "G#6/Ab6");
		frequencyToNoteMap.put(1760.0, "A6");
		frequencyToNoteMap.put(1864.66, "A#6/Bb6");
		frequencyToNoteMap.put(1975.53, "B6");
		frequencyToNoteMap.put(2093.0, "C7");
		frequencyToNoteMap.put(2217.46, "C#7/Db7");
		frequencyToNoteMap.put(2349.32, "D7");
		frequencyToNoteMap.put(2489.02, "D#7/Eb7");
		frequencyToNoteMap.put(2637.02, "E7");
		frequencyToNoteMap.put(2793.83, "F7");
		frequencyToNoteMap.put(2959.96, "F#7/Gb7");
		frequencyToNoteMap.put(3135.96, "G7");
		frequencyToNoteMap.put(3322.44, "G#7/Ab7");
		frequencyToNoteMap.put(3520.0, "A7");
		frequencyToNoteMap.put(3729.31, "A#7/Bb7");
		frequencyToNoteMap.put(3951.07, "B7");
		frequencyToNoteMap.put(4186.01, "C8");
		frequencyToNoteMap.put(4434.92, "C#8/Db8");
		frequencyToNoteMap.put(4698.63, "D8");
		frequencyToNoteMap.put(4978.03, "D#8/Eb8");
		frequencyToNoteMap.put(5274.04, "E8");
		frequencyToNoteMap.put(5587.65, "F8");
		frequencyToNoteMap.put(5919.91, "F#8/Gb8");
		frequencyToNoteMap.put(6271.93, "G8");
		frequencyToNoteMap.put(6644.88, "G#8/Ab8");
		frequencyToNoteMap.put(7040.0, "A8");
		frequencyToNoteMap.put(7458.62, "A#8/Bb8");
		frequencyToNoteMap.put(7902.13, "B8");

		for (double currFreq : frequencyToNoteMap.keySet())
		{
			noteFrequencies.add(currFreq);
		}

	}

	/**
	 * Deprecated
	 */
	public String calculateNote(double frequency)
	{
		double minDistance = Double.MAX_VALUE;
		double closestFreq = 0;
		for (double currFreq : noteFrequencies)
		{
			double currDistance = Math.abs(currFreq - frequency);
			if(currDistance < minDistance)
			{
				minDistance = currDistance;
				closestFreq = currFreq;
			}
		}

		return frequencyToNoteMap.get(closestFreq);
	}

	public static Note getNoteFromMIDI(double midi)
	{
		if(midi < 0)
			return null;
		if(midi > MAX_MIDI)
			return null;
		int midiInt = (int)Math.round(midi);
		int octave = midiInt / 12;
		String noteName = midiTranslationArray[midiInt % 12];
		return new Note(noteName.charAt(0), octave, noteName.length() > 1);
	}
	
	public static double getMIDIFromNote(Note note)
	{
		int n = 0;
		for(int i = 0; i < midiTranslationArray.length; i++)
		{
			if(midiTranslationArray[i].equals(note.note + (note.isSharp ? "#" : "")))
			{
				n = i;
				break;
			}
		}
		
		return (note.octave * 12) + n;
	}

	private static String[] midiTranslationArray = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
}

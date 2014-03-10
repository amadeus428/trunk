package com.cs429.amadeus.test.junit;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.helpers.NoteCalculator;

import junit.framework.TestCase;

public class NoteCalculatorTest extends TestCase {
	Note testNoteA5;
	Note testNoteMax;
	@Override
	public void setUp() throws Exception {
		testNoteA5 = new Note("A5");
		testNoteMax = new Note("G10");
	}

	public void testIntegerMidiValue() {
		System.out.println(NoteCalculator.getNoteFromMIDI(69).toString());
		assertEquals(testNoteA5, NoteCalculator.getNoteFromMIDI(69));
	}
	
	public void testClosestNote() {
		assertEquals(testNoteA5, NoteCalculator.getNoteFromMIDI(69.49));
	}
	
	public void testCalculateNoteNegative() {
		assertEquals(null, NoteCalculator.getNoteFromMIDI(-1));
	}
	
	public void testCalculateNoteMax() {
		assertEquals(testNoteMax, NoteCalculator.getNoteFromMIDI(127));
	}
	public void testAboveMax(){
		assertEquals(null, NoteCalculator.getNoteFromMIDI(128));
	}
}

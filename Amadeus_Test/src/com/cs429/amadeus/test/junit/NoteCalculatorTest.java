package com.cs429.amadeus.test.junit;

import com.cs429.amadeus.NoteCalculator;

import junit.framework.TestCase;

public class NoteCalculatorTest extends TestCase {

	private NoteCalculator testCalculator;
	@Override
	public void setUp() throws Exception {
		
		testCalculator = new NoteCalculator();
	}

	public void testCalculateNoteZero() {
		assertEquals("C0", testCalculator.calculateNote(0));
	}
	
	public void testCalculateNote() {
		assertEquals("G0", testCalculator.calculateNote(25));
	}
	
	public void testCalculateNoteNegative() {
		assertEquals("C0", testCalculator.calculateNote(-10));
	}
	
	public void testCalculateNoteMax() {
		assertEquals("B8", testCalculator.calculateNote(8000));
	}
}

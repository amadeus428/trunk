package com.cs429.amadeus.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.cs429.amadeus.MainActivity;
import com.cs429.amadeus.NoteCalculator;

import junit.framework.TestCase;

public class NoteCalculatorTest extends ActivityInstrumentationTestCase2<MainActivity> {
	
	private MainActivity mActivity;
	private NoteCalculator testCalculator;
	
	public NoteCalculatorTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		testCalculator = new NoteCalculator();
	    mActivity = getActivity();
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

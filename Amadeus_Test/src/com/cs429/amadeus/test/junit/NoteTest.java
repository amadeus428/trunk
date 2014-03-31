package com.cs429.amadeus.test.junit;

import com.cs429.amadeus.Note;

import junit.framework.TestCase;

public class NoteTest extends TestCase {

	public void testNoteConstructor(){
		assertEquals(new Note('A', 5, false, Note.QUARTER_NOTE).toString(), "A5");
		assertEquals(new Note('A', 5, true, Note.QUARTER_NOTE).toString(), "A#5");

	}
	public void testNoteStringConstructor(){
		assertEquals(new Note('A', 5, false, Note.QUARTER_NOTE), new Note("A5", Note.QUARTER_NOTE));
	}
	
}

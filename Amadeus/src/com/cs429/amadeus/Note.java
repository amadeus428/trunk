package com.cs429.amadeus;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Note {

	char note;
	int octave;
	boolean isSharp;

	public Note(String note) {
		this.note = note.charAt(0);
		if (note.charAt(1) == '#') {
			this.isSharp = true;
			this.octave = Integer.parseInt(note.substring(2));
		} else {
			this.isSharp = false;
			this.octave = Integer.parseInt(note.substring(1));
		}
	}

	@Override
	public String toString() {
		return note + (isSharp ? "#" : "") + octave;

	}
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == Note.class)
			return this.toString().equals(((Note)obj).toString());
		else
			return false;
	}

	public Note(char noteLetter, int octave, boolean isSharp) {
		this.note = noteLetter;
		this.octave = octave;
		this.isSharp = isSharp;
	}

	public void draw(Canvas canvas, Rect rect) {

	}
}

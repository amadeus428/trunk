package com.cs429.amadeus;

import android.graphics.Canvas;
import android.graphics.Rect;


public class Note {
	
	
	char note;
	int octave;
	boolean isSharp;
	
	
	public Note(String note) {
	}

	
	public Note(char noteLetter, int octave, boolean isSharp) {
		this.note = noteLetter;
		this.octave = octave;
		this.isSharp = isSharp;
	}


	public void draw(Canvas canvas, Rect rect){
		
	}
}

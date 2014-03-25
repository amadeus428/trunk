package com.cs429.amadeus;

public class Note
{
	public static final int WHOLE_NOTE = 0;
	public static final int HALF_NOTE_DOWN = 1;
	public static final int QUARTER_NOTE_DOWN = 2;
	public static final int EIGHTH_NOTE_DOWN = 3;
	public static final int SIXTEENTH_NOTE_DOWN = 4;
	
	public char note;
	public int octave;
	public boolean isSharp;
	public int type;

	public Note(String note, int type)
	{
		this.note = note.charAt(0);
		if(note.charAt(1) == '#')
		{
			this.isSharp = true;
			this.octave = Integer.parseInt(note.substring(2));
		}
		else
		{
			this.isSharp = false;
			this.octave = Integer.parseInt(note.substring(1));
		}
		
		this.type = type;
	}
	
	public Note(char noteLetter, int octave, boolean isSharp, int type)
	{
		this.note = noteLetter;
		this.octave = octave;
		this.isSharp = isSharp;
		this.type = type;
	}

	@Override
	public String toString()
	{
		return note + (isSharp ? "#" : "") + octave;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj.getClass() == Note.class)
		{
			return toString().equals(((Note)obj).toString());
		}

		return false;
	}
}

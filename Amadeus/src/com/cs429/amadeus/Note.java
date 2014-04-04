
package com.cs429.amadeus;

/**
 * This class represents a single staff note.
 */
public class Note
{
	public static final int WHOLE_NOTE = 0;
	public static final int HALF_NOTE = 1;
	public static final int QUARTER_NOTE = 2;
	public static final int EIGHTH_NOTE = 3;
	public static final int SIXTEENTH_NOTE = 4;

	public char note;
	public int octave;
	public boolean isSharp;
	public int type;

	/**
	 * @param note
	 *            - note + [#] + octave
	 * @param type
	 *            - note type constant from {@link Note}
	 */
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

	/**
	 * @param noteLetter
	 *            - A-G
	 * @param octave
	 *            - any integer > 0
	 * @param isSharp
	 *            - true if note is a sharp or false otherwise
	 * @param type
	 *            - note type constant from {@link Note}
	 */
	public Note(char noteLetter, int octave, boolean isSharp, int type)
	{
		this.note = noteLetter;
		this.octave = octave;
		this.isSharp = isSharp;
		this.type = type;
	}

	/**
	 * Returns note + [#] + octave.
	 */
	@Override
	public String toString()
	{
		return note + (isSharp ? "#" : "") + octave;
	}

	/**
	 * Makes a comparison based on {@link #toString() toString} values.
	 */
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

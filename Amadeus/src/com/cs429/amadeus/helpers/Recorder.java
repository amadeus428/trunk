
package com.cs429.amadeus.helpers;

import java.util.Calendar;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.views.PlayAlongStaffLayout;
import com.cs429.amadeus.views.StaffLayout;

/**
 * This class is used to process midi notes from PDService.
 */
public abstract class Recorder
{
	private float bps;
	private float period;
	private long lastRecordedNoteTime = 0;
	private boolean isRecording = false;
	private Note lastNote;
	private final StaffLayout staffLayout;

	public Recorder(StaffLayout staffLayout, int bpm)
	{
		this.staffLayout = staffLayout;
		bps = bpm / 60.f;
		period = (int)((1.0 / bps) * 1000 / 4.0);
	}

	/**
	 * Called when a note is fully determined.
	 * 
	 * @param note
	 *            - the note determined
	 */
	public abstract void onNote(Note note);

	/**
	 * If recording, processes the PDService midiNote.
	 * 
	 * @param midiNote
	 *            - the midiNote given from PDService
	 */
	public void tryRecordFloat(float midiNote, boolean isPlayAlong)
	{
		if(!isRecording)
		{
			return;
		}

		long currTime = Calendar.getInstance().getTimeInMillis();
		if(currTime - lastRecordedNoteTime > period)
		{
			Note note = NoteCalculator.getNoteFromMIDI((double)midiNote);
			if(note.octave >= 2 && note.octave <= 8)
			{
				if(lastNote != null)
				{
					lastNote.type = getNoteTypeOfLastNote();

					if(isPlayAlong)
					{
						((PlayAlongStaffLayout)staffLayout).addRecordedNote(lastNote);
					}
					else
					{
						staffLayout.addNote(lastNote);
					}
				}
				lastNote = note;
				lastRecordedNoteTime = currTime;

				onNote(note);
			}
		}
	}

	public void start()
	{
		isRecording = true;
	}

	public void stop()
	{
		isRecording = false;
	}

	public boolean isRecording()
	{
		return isRecording;
	}

	private int getNoteTypeOfLastNote()
	{
		int ms = (int)((1.0f / bps) * 1000);
		long currTime = Calendar.getInstance().getTimeInMillis();
		float timeSinceLastNote = currTime - lastRecordedNoteTime;

		// Go through each different note type's duration.
		// Find the one that most closely matches timeSinceLastNote.
		float best = Float.MAX_VALUE;
		float besti = 0;
		for (float i = 4.0f; i >= .025f; i /= 2.0f)
		{
			float val = ms * i;
			float diff = Math.abs(val - timeSinceLastNote);
			if(diff < best)
			{
				best = diff;
				besti = i;
			}
		}

		if(besti == 4.0f)
		{
			return Note.WHOLE_NOTE;
		}
		else if(besti == 2.0f)
		{
			return Note.HALF_NOTE;
		}
		else if(besti == 1.0f)
		{
			return Note.QUARTER_NOTE;
		}
		else if(besti == 0.5f)
		{
			return Note.EIGHTH_NOTE;
		}
		else
		{
			return Note.SIXTEENTH_NOTE;
		}
	}
}

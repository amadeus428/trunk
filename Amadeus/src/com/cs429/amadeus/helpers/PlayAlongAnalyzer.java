package com.cs429.amadeus.helpers;

import java.util.ArrayList;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.views.NoteView;
import com.cs429.amadeus.views.PlayAlongStaffLayout;

public class PlayAlongAnalyzer
{
	public static float getPlayAlongScore(PlayAlongStaffLayout staffLayout)
	{
		ArrayList<NoteView> nonRecordedNoteViews = staffLayout.getAllNonRecordedNoteViews();
		ArrayList<NoteView> recordedNoteViews = staffLayout.getAllRecordedNoteViews();
		int minSize = Math.min(nonRecordedNoteViews.size(), recordedNoteViews.size());
		final float POINTS_PER_NOTE = 100.0f / minSize;
		final float STEPS_DIFF_WEIGHT = POINTS_PER_NOTE * 0.75f;
		final float TYPE_DIFF_WEIGHT = POINTS_PER_NOTE * 0.20f;
		final float SHARP_DIFF_WEIGHT = POINTS_PER_NOTE * 0.05f;

		float score = 0;
		for (int i = 0; i < minSize; i++)
		{
			NoteView nonRecordedNoteView = nonRecordedNoteViews.get(i);
			NoteView recordedNoteView = recordedNoteViews.get(i);
			Note nonRecordedNote = nonRecordedNoteView.getNote();
			Note recordedNote = recordedNoteView.getNote();
			int stepSize = staffLayout.getSpaceHeight() / 2;
			int stepsDiff = (int)Math.abs(nonRecordedNoteView.getY() - recordedNoteView.getY()) / stepSize;
			int typeDiff = getTypeDiff(nonRecordedNote, recordedNote);
			int sharpsDiffPenalty = nonRecordedNote.isSharp == recordedNote.isSharp ? 0 : 1;

			float points = POINTS_PER_NOTE - (STEPS_DIFF_WEIGHT * getStepsDiffPenalty(stepsDiff)) - 
					(TYPE_DIFF_WEIGHT * (typeDiff / 4.0f)) - (SHARP_DIFF_WEIGHT * sharpsDiffPenalty);
			score += points;
		}

		return score;
	}
	
	private static float getStepsDiffPenalty(int stepsDiff)
	{
		stepsDiff = Math.min(10, stepsDiff);
		return stepsDiff / 15.0f;
	}

	private static int getTypeDiff(Note note1, Note note2)
	{
		return Math.abs(getTypeId(note1.type) - getTypeId(note2.type));
	}

	private static int getTypeId(int type)
	{
		switch (type)
		{
			case Note.WHOLE_NOTE:
				return 4;
			case Note.HALF_NOTE:
				return 3;
			case Note.QUARTER_NOTE:
				return 2;
			case Note.EIGHTH_NOTE:
				return 1;
			case Note.SIXTEENTH_NOTE:
				return 0;
		}

		return -1;
	}
}

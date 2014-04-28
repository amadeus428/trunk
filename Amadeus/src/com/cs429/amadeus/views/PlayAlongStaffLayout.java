package com.cs429.amadeus.views;

import java.util.ArrayList;

import com.cs429.amadeus.Note;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.HorizontalScrollView;

/**
 * This class is a modification of {@link StaffLayout} that distinguishes
 * between recorded and opened notes.
 */
public class PlayAlongStaffLayout extends StaffLayout {
	private int numRecordedNotes = 0;

	public PlayAlongStaffLayout(Context context) {
		super(context);
		setEnabled(false);
	}

	public PlayAlongStaffLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEnabled(false);
	}

	public PlayAlongStaffLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setEnabled(false);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		// Disable all touches.
		return true;
	}

	/**
	 * Do nothing because manually adding notes is not supported in this layout.
	 */
	@Override
	public void addNote(int x, int y) {
	}

	/**
	 * This should only be used for adding a note via open dialog.
	 */
	@Override
	public void addNote(Note note) {
		adjustNoteSize(note.type);

		int x = (getChildCount() * noteSpacing) + noteSpacing;
		int y = getSnappedYFromNote(note);
		addNote(note, x, y, false);
	}

	/**
	 * This should only be used for adding a note via recording.
	 * 
	 * @param note
	 *            - the note to add
	 */
	public void addRecordedNote(Note note) {
		adjustNoteSize(note.type);

		int x = (numRecordedNotes * noteSpacing) + noteSpacing;
		int y = getSnappedYFromNote(note);
		addNote(note, x, y, true);

		numRecordedNotes++;
	}

	/**
	 * Gets all non-recorded (opened) note views.
	 * 
	 * @return - all non-recorded note views
	 */
	public ArrayList<NoteView> getAllNonRecordedNoteViews() {
		ArrayList<NoteView> noteViews = new ArrayList<NoteView>();
		for (int i = 0; i < getChildCount(); i++) {
			NoteView noteView = (NoteView) getChildAt(i);
			if (noteView.getAlpha() != 1) {
				noteViews.add(noteView);
			}
		}

		return noteViews;
	}

	/**
	 * Gets all recorded note views.
	 * 
	 * @return - all recorded note views
	 */
	public ArrayList<NoteView> getAllRecordedNoteViews() {
		ArrayList<NoteView> noteViews = new ArrayList<NoteView>();
		for (int i = 0; i < getChildCount(); i++) {
			NoteView noteView = (NoteView) getChildAt(i);
			if (noteView.getAlpha() == 1) {
				noteViews.add(noteView);
			}
		}

		return noteViews;
	}

	/**
	 * Clears all recorded note views.
	 */
	public void clearAllRecordedNoteViews() {
		numRecordedNotes = 0;

		// This is a slow way to do this but couldn't get it working the obvious
		// way.
		ArrayList<NoteView> nonRecordedNoteViews = getAllNonRecordedNoteViews();
		clearAllNoteViews();
		for (NoteView noteView : nonRecordedNoteViews) {
			addNote(noteView.getNote());
		}
	}

	protected void addNote(Note note, int x, int y, boolean isRecorded) {
		NoteView noteView = new NoteView(getContext(), this, note);

		// May need to correct everything due to sharps and ledger lines.
		int yCorrection = 0;
		int widthCorrection = 0;
		int heightCorrection = Math.max(0, getNumLedgerLines(note) - 0)
				* spaceHeight;
		if (noteView.getNote().isSharp) {
			yCorrection = (int) (-sharpHeight * .33f);
			widthCorrection = (int) (noteWidth * 1.1f);
			boolean isWhole = note.type == Note.WHOLE_NOTE;
			heightCorrection += isWhole ? (int) (sharpHeight * .66f)
					: (int) (sharpHeight * .33f);
		}
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(
				noteWidth + widthCorrection, noteHeight + heightCorrection, x,
				y + yCorrection);

		if (!isRecorded) {
			// Non-recorded notes are half transparent.
			noteView.setAlpha(.5f);
		}

		addView(noteView, getAddPos(x), lp);
		invalidate();

		if (isRecorded) {
			// This assumes that the parent of this view is a
			// HorizontalScrollView.
			int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
			HorizontalScrollView parent = ((HorizontalScrollView) getParent());
			if (x - parent.getScrollX() > screenWidth) {
				scrollRight(null);
			}
		}
	}
}

package com.cs429.amadeus.helpers;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.puredata.core.PdBase;

import android.app.Activity;
import android.util.Log;
import android.widget.HorizontalScrollView;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.views.NoteView;
import com.cs429.amadeus.views.StaffLayout;

/**
 * This class plays (and animates) a staff's notes.
 */
public abstract class StaffMIDIPlayer {
    protected ArrayList<NoteView> noteViews;
    protected Activity parentActivity;
    protected StaffLayout staffLayout;
    private int bpm;
    private int numBeatsPassed = 0;
    private int currNoteIndex = 0;
    private boolean isPlaying = false;
    private Timer timer = new Timer();

    public StaffMIDIPlayer(Activity parentActivity, StaffLayout staffLayout, int bpm) {
	this.parentActivity = parentActivity;
	this.staffLayout = staffLayout;
	this.bpm = bpm;

	noteViews = staffLayout.getAllNoteViews();
    }

    /**
     * Called after the last note has been played.
     */
    public abstract void onFinished();

    /**
     * Plays every note on the staff from left to right. Also changes the
     * appearance of the current playing note's {@link NoteView}.
     */
    public void play() {
	numBeatsPassed = 0;
	currNoteIndex = 0;

	// Adjust the durations by 1/4 so the timer fires as quickly as 1/16
	// notes.
	float bps = (bpm / 60.0f);
	int ms = (int) ((1.0f / bps) * 1000 / 4.0f);

	isPlaying = true;
	timer.scheduleAtFixedRate(new TimerTask() {
	    @Override
	    public void run() {
		NoteView lastNoteView = null;
		if (currNoteIndex > 0) {
		    lastNoteView = noteViews.get(currNoteIndex - 1);
		    setNoteViewAlpha(lastNoteView, 1.0f);
		}

		if (!isPlaying || currNoteIndex >= noteViews.size()) {
		    isPlaying = false;
		    cancel();
		    onFinished();
		    return;
		}

		numBeatsPassed++;
		NoteView currNoteView = noteViews.get(currNoteIndex);
		Note lastNote = lastNoteView == null ? null : lastNoteView.getNote();
		if (numBeatsPassed < getAdjustedNumBeats(lastNote)) {
		    return;
		}
		numBeatsPassed = 0;

		tryScrollRight(currNoteView);
		setNoteViewAlpha(currNoteView, .5f);

		Note currNote = currNoteView.getNote();
		float midiNote = (float) NoteCalculator.getMIDIFromNote(currNote);

		PdBase.sendFloat("midinote", midiNote);
		PdBase.sendBang("trigger");

		currNoteIndex++;
	    }
	}, 0, ms);
    }

    /**
     * Stops playing notes after the current note has been played.
     */
    public void stop() {
	isPlaying = false;
	onFinished();
    }

    /**
     * Returns whether or not notes are being played.
     * 
     * @return - if notes are being played or not
     */
    public boolean isPlaying() {
	return isPlaying;
    }

    protected void setNoteViewAlpha(final NoteView noteView, final float alpha) {
	parentActivity.runOnUiThread(new Runnable() {
	    @Override
	    public void run() {
		noteView.setAlpha(alpha);
	    }
	});
    }

    private void tryScrollRight(NoteView noteView) {
	// This assumes that the StaffLayout's parent is a HorizontalScrollView.
	int screenWidth = parentActivity.getResources().getDisplayMetrics().widthPixels;
	HorizontalScrollView parent = (HorizontalScrollView) staffLayout.getParent();
	if (noteView.getX() - parent.getScrollX() > screenWidth) {
	    staffLayout.scrollRight(null);
	}
    }

    private int getAdjustedNumBeats(Note note) {
	if (note == null) {
	    return 0;
	}

	switch (note.type) {
	case Note.WHOLE_NOTE:
	    return 16;
	case Note.HALF_NOTE:
	    return 8;
	case Note.QUARTER_NOTE:
	    return 4;
	case Note.EIGHTH_NOTE:
	    return 2;
	case Note.SIXTEENTH_NOTE:
	    return 1;
	}

	return 1;
    }
}

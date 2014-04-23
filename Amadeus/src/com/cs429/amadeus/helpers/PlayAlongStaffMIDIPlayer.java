package com.cs429.amadeus.helpers;

import android.app.Activity;

import com.cs429.amadeus.views.NoteView;
import com.cs429.amadeus.views.PlayAlongStaffLayout;
import com.cs429.amadeus.views.StaffLayout;

/**
 * This is a simple modification of {@link StaffMidiPlayer} that only plays
 * opened notes.
 */
public abstract class PlayAlongStaffMIDIPlayer extends StaffMIDIPlayer {
    public PlayAlongStaffMIDIPlayer(Activity parentActivity,
	    StaffLayout staffLayout, int bpm) {
	super(parentActivity, staffLayout, bpm);

	// Only play notes that were opened via dialog (all non-recorded notes).
	noteViews = ((PlayAlongStaffLayout) staffLayout)
		.getAllNonRecordedNoteViews();
    }

    @Override
    protected void setNoteViewAlpha(final NoteView noteView, final float alpha) {
	// By default, non-recorded notes are partly transparent.
	// So, we just need to use the opposite alpha when we flash them.
	final float flippedAlpha = alpha == 1.0f ? .5f : 1.0f;
	parentActivity.runOnUiThread(new Runnable() {
	    @Override
	    public void run() {
		noteView.setAlpha(flippedAlpha);
	    }
	});
    }
}

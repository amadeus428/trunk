package com.cs429.amadeus.helpers;

import android.app.Activity;

import com.cs429.amadeus.views.NoteView;
import com.cs429.amadeus.views.PlayAlongStaffLayout;
import com.cs429.amadeus.views.StaffLayout;

public abstract class PlayAlongStaffMIDIPlayer extends StaffMIDIPlayer {
    public PlayAlongStaffMIDIPlayer(Activity parentActivity, StaffLayout staffLayout, int bpm) {
	super(parentActivity, staffLayout, bpm);

	noteViews = ((PlayAlongStaffLayout) staffLayout).getAllNonRecordedNoteViews();
    }

    @Override
    protected void setNoteViewAlpha(final NoteView noteView, final float alpha) {
	final float flippedAlpha = alpha == 1.0f ? .5f : 1.0f;
	parentActivity.runOnUiThread(new Runnable() {
	    @Override
	    public void run() {
		noteView.setAlpha(flippedAlpha);
	    }
	});
    }
}

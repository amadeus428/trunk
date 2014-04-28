package com.cs429.amadeus.helpers;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.puredata.core.PdBase;

import android.app.Activity;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
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
	private float bpm;
	private int numBeatsPassed = 0;
	private int currNoteIndex = 0;
	private boolean isPlaying = false;
	private Timer timer = new Timer();
	private SoundProfile soundProfile;

	public StaffMIDIPlayer(Activity parentActivity, StaffLayout staffLayout,
			SoundProfile soundProfile, float bpm) {
		this.parentActivity = parentActivity;
		this.staffLayout = staffLayout;
		this.soundProfile = soundProfile;
		this.bpm = bpm;

		noteViews = staffLayout.getAllNoteViews();
	}

	/**
	 * Called after the last note has been played.
	 */
	public abstract void onFinished();

	/**
	 * Plays every note on the staff from left to right, using PureData. Also
	 * changes the appearance of the current playing note's {@link NoteView}. If
	 * audioFilePath is null, PureData will be used to play the notes.
	 * 
	 * @param audioFilePath
	 *            - path to the audio file to play
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
				Note lastNote = lastNoteView == null ? null : lastNoteView
						.getNote();
				if (numBeatsPassed < getAdjustedNumBeats(lastNote)) {
					return;
				}
				numBeatsPassed = 0;

				tryScrollRight(currNoteView);
				setNoteViewAlpha(currNoteView, .5f);

				Note currNote = currNoteView.getNote();
				float midiNote = (float) NoteCalculator
						.getMIDIFromNote(currNote);

				if (soundProfile == null) {
					PdBase.sendFloat("midinote", midiNote);
					PdBase.sendBang("trigger");
				} else {
					float freq = NoteCalculator.getFreqFromMIDI(midiNote);
					soundProfile.play(freq);
				}

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
		HorizontalScrollView parent = (HorizontalScrollView) staffLayout
				.getParent();
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

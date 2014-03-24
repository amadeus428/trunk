package com.cs429.amadeus.helpers;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.puredata.core.PdBase;

import android.app.Activity;
import android.widget.HorizontalScrollView;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.views.NoteView;
import com.cs429.amadeus.views.StaffLayout;

public abstract class StaffMIDIPlayer
{
	private int noteCooldown;
	private int currNoteIndex = 0;
	private boolean isPlaying = false;
	private Activity parentActivity;
	private StaffLayout staffLayout;
	private ArrayList<NoteView> noteViews;
	private Timer timer = new Timer();
	
	public StaffMIDIPlayer(Activity parentActivity, StaffLayout staffLayout, int noteCooldown)
	{
		this.parentActivity = parentActivity;
		this.staffLayout = staffLayout;
		this.noteViews = staffLayout.getAllNoteViews();
		this.noteCooldown = noteCooldown;
	}
	
	/**
	 * Called after the last note has been played.
	 */
	public abstract void onFinished();
	
	/**
	 * Plays every note on the staff from left to right.
	 * Also changes the appearance of the current playing note's {@link @NoteView}.
	 */
	public void play()
	{
		isPlaying = true;		
		timer.scheduleAtFixedRate(new TimerTask() 
		{
			@Override
			public void run() 
			{	
				if(currNoteIndex > 0)
				{
					NoteView lastNoteView = noteViews.get(currNoteIndex - 1);
					setNoteViewAlpha(lastNoteView, 1.0f);
				}
				
				if(!isPlaying || currNoteIndex >= noteViews.size())
				{
					isPlaying = false;
					cancel();
					onFinished();
					return;
				}
				
				NoteView currNoteView = noteViews.get(currNoteIndex);
				tryScrollRight(currNoteView);			
				setNoteViewAlpha(currNoteView, .5f);
				
				Note currNote = currNoteView.getNote();
				float midiNote = (float)NoteCalculator.getMIDIFromNote(currNote);
				
				PdBase.sendFloat("midinote", midiNote);
				PdBase.sendBang("trigger");
				
				currNoteIndex++;
			}
		}, 0, noteCooldown);
	}
	
	/**
	 * Stops playing notes after the current note has been played.
	 */
	public void stop()
	{
		isPlaying = false;
		onFinished();
	}
	
	/**
	 * Returns whether or not notes are being played.
	 * @return - if notes are being played or not
	 */
	public boolean isPlaying()
	{
		return isPlaying;
	}
	
	private void setNoteViewAlpha(final NoteView noteView, final float alpha)
	{
		parentActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				noteView.setAlpha(alpha);
			}		
		});
	}
	
	private void tryScrollRight(NoteView noteView)
	{
		int screenWidth = parentActivity.getResources().getDisplayMetrics().widthPixels;
		HorizontalScrollView parent = (HorizontalScrollView)staffLayout.getParent();
		if(noteView.getX() - parent.getScrollX() > screenWidth)
		{
			staffLayout.scrollRight(null);
		}
		
	}
}

package com.cs429.amadeus.helpers;

import java.util.Timer;
import java.util.TimerTask;

import com.cs429.amadeus.R;

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

public class Metronome
{
	private int bpm;
	private Activity parentActivity;
	private Timer tickTimer;
	
	public Metronome(Activity parentActivity, int bpm)
	{
		this.parentActivity = parentActivity;
		this.bpm = bpm;
	}

	public void start()
	{
		float bps = bpm / 60.0f;
		final int ms = (int)((1.0 / bps) * 1000); 
		
		tickTimer = new Timer();
		tickTimer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				parentActivity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{	
						doMetronomeFlash(ms);
					}			
				});
			}				
		}, 0, ms);
	}
	
	public void stop()
	{
		final TextView noteRecordedTextView = (TextView)parentActivity.findViewById(R.id.fragment_recording_note_recorded_textview);
		noteRecordedTextView.setBackgroundColor(Color.TRANSPARENT);
		tickTimer.cancel();
	}
	
	private void doMetronomeFlash(final int ms)
	{
		final TextView noteRecordedTextView = (TextView)parentActivity.findViewById(R.id.fragment_recording_note_recorded_textview);
		
		final Timer flashTimer = new Timer();
		final TimerTask flashTimerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				parentActivity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						noteRecordedTextView.setBackgroundColor(Color.TRANSPARENT);
					}
				});
			}			
		};
		

		noteRecordedTextView.setBackgroundColor(Color.RED);
		flashTimer.schedule(flashTimerTask, ms / 8);
	}
}

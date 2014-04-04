
package com.cs429.amadeus.helpers;

import java.util.Timer;
import java.util.TimerTask;

import com.cs429.amadeus.R;

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

/**
 * This class flashes the color of the "note recorded" text view in
 * {@link RecordingFragment}, once per beat, based on its given bpm.
 */
public abstract class Metronome
{
	private int bpm;
	private Activity parentActivity;
	private Timer tickTimer;

	public Metronome(Activity parentActivity, int bpm)
	{
		this.parentActivity = parentActivity;
		this.bpm = bpm;
	}

	public abstract void onTickStart();

	public abstract void onTickEnd();

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
		onTickEnd();
		tickTimer.cancel();
	}

	private void doMetronomeFlash(final int ms)
	{
		// final TextView noteRecordedTextView =
		// (TextView)parentActivity.findViewById(R.id.fragment_recording_note_recorded_textview);

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
						// noteRecordedTextView.setBackgroundColor(Color.TRANSPARENT);
						onTickEnd();
					}
				});
			}
		};

		// noteRecordedTextView.setBackgroundColor(Color.RED);
		onTickStart();
		flashTimer.schedule(flashTimerTask, ms / 8);
	}
}


package com.cs429.amadeus.fragments;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.R;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.helpers.NoteCalculator;
import com.cs429.amadeus.helpers.StaffMIDIPlayer;
import com.cs429.amadeus.views.StaffLayout;

public class RecordingFragment extends Fragment
{
	private long lastRecordedNoteTime = 0;
	private boolean isRecording = false;
	private boolean ignoreRecordingNoise = true;
	private Note lastNote;
	private ImageButton playStopNotesButton; // need to be able to change its text
	private Spinner bpmSpinner; // need to be able to get its selected value
	private Timer metronomeTimer = new Timer();
	private StaffLayout staffLayout;
	private StaffMIDIPlayer midiPlayer;
	private AlertDialog.Builder saveDialog; 
	private PdUiDispatcher dispatcher;
	private PdService pdService = null;
	
	private final ServiceConnection pdConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			pdService = ((PdService.PdBinder)service).getService();
			try
			{
				initPd();
				loadPatch();
			}
			catch (IOException e)
			{
				Log.e("TAG", e.toString());
				getActivity().finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			// This method will never be called.
		}
	};

	public RecordingFragment()
	{
		// Empty constructor required for fragment subclasses.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_recording, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("Record");

		staffLayout = (StaffLayout)getActivity().findViewById(R.id.fragment_recording_staff_layout);
		bpmSpinner = (Spinner)getActivity().findViewById(R.id.fragment_recording_bpm_spinner);
		ArrayAdapter<CharSequence> bpmSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.bpm_items, R.drawable.spinner_item);
		bpmSpinnerAdapter.setDropDownViewResource(R.drawable.spinner_item);
		bpmSpinner.setAdapter(bpmSpinnerAdapter);
		createButtonListeners();

		initSystemServices();
		getActivity().bindService(new Intent(getActivity(), PdService.class), pdConnection,
				Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onStop()
	{
		super.onDestroy();
		getActivity().unbindService(pdConnection);
	}
	
	private void createButtonListeners()
	{
		final Button saveButton = (Button)getActivity().findViewById(R.id.fragment_recording_save_recording_button);
		saveButton.setOnClickListener(new OnClickListener()
		{		
			@Override
			public void onClick(View view)
			{	
				createSaveDialog();
				saveDialog.show();
			}		
		});
		
		final Button startStopRecordingButton = (Button)getActivity().findViewById(R.id.fragment_recording_start_stop_recording_button);
		startStopRecordingButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{	
				// If currently playing notes, don't allow user to record.
				if(!isPlaying())
				{
					String newText = isRecording ? "Record" : "Stop";
					startStopRecordingButton.setText(newText);
					
					lastRecordedNoteTime = 0;
					lastNote = null;
					isRecording = !isRecording;
					
					if(isRecording)
					{
						startMetronome();
					}
					else
					{
						stopMetronome();
					}
				}
			}			
		});
		
		playStopNotesButton = (ImageButton)getActivity().findViewById(R.id.fragment_recording_play_stop_notes_button);
		playStopNotesButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				// If currently recording, don't allow user to play notes.
				if(isRecording)
				{
					return;
				}
				
				if(isPlaying())
				{
					playStopNotesButton.setImageResource(R.drawable.play);
					midiPlayer.stop();
				}
				else
				{
					playStopNotesButton.setImageResource(R.drawable.stop);
					playNotes();
				}
			}			
		});
		
		final Button clearNotesButton = (Button)getActivity().findViewById(R.id.fragment_recording_clear_notes_button);
		clearNotesButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				// If playing or recording notes, don't allow user to clear the notes.
				if(!isPlaying() && !isRecording)
				{
					RecordingFragment.this.staffLayout.clearAllNoteViews();
					TextView noteRecorded = (TextView)getActivity().findViewById(R.id.fragment_recording_note_recorded_textview);
					noteRecorded.setText("Note recorded: ");
				}
			}			
		});
	}
	
	private void startMetronome()
	{
		// Flash once per beat.
		int bpm = getBPM();
		float bps = bpm / 60.0f;
		final int ms = (int)((1.0 / bps) * 1000); 	
		
		metronomeTimer = new Timer();
		metronomeTimer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				RecordingFragment.this.getActivity().runOnUiThread(new Runnable()
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
	
	private void doMetronomeFlash(final int ms)
	{
		final TextView noteRecordedTextView = (TextView)getActivity().findViewById(R.id.fragment_recording_note_recorded_textview);
		
		final Timer flashTimer = new Timer();
		final TimerTask flashTimerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				RecordingFragment.this.getActivity().runOnUiThread(new Runnable()
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
	
	private void stopMetronome()
	{
		final TextView noteRecordedTextView = (TextView)getActivity().findViewById(R.id.fragment_recording_note_recorded_textview);
		noteRecordedTextView.setBackgroundColor(Color.TRANSPARENT);
		metronomeTimer.cancel();
	}
	
	private void playNotes()
	{
		// While playing notes, disable touch events on the staff.
		staffLayout.setEnabled(false);
		
		int bpm = getBPM();
		midiPlayer = new StaffMIDIPlayer(getActivity(), staffLayout, bpm)
		{
			@Override
			public void onFinished()
			{
				RecordingFragment.this.getActivity().runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						playStopNotesButton.setImageResource(R.drawable.play);
						
						// Re-enable touch events on the staff.
						staffLayout.setEnabled(true);
					}
					
				});
			}
		};
		midiPlayer.play();
	}
	
	private void createSaveDialog()
	{	
		final EditText input = new EditText(getActivity());
		saveDialog = new AlertDialog.Builder(getActivity());
		saveDialog.setTitle("Save recording")
		.setMessage("Choose save filename")
		.setView(input)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton) 
			{
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				dialog.cancel();
			}
		});
	}

	private void initPd() throws IOException
	{	
		// Configure the audio glue.
		AudioParameters.init(getActivity());
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 1, 2, 10.0f);
		startPDService();

		// Create and install the dispatcher.
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter()
		{
			@Override
			public void receiveFloat(String source, final float midiNote)
			{
				if(!isRecording || isPlaying())
				{
					return;
				}
				
				int bpm = getBPM();
				float bps = bpm / 60.0f;
	
				// This gives us the amount of time after a 1/16 note.
				// 1/16 notes are the quickest notes we allow for this applcation.
				// Thus, we only need to record that often.
				int ms = (int)((1.0 / bps) * 1000 / 4.0); 				
				long currTime = Calendar.getInstance().getTimeInMillis();
				if(currTime - lastRecordedNoteTime > ms)
				{
					updateStaffView(midiNote);
					lastRecordedNoteTime = currTime;
				}
			}
		});
	}

	private void updateStaffView(float midiNote)
	{		
		Note note = NoteCalculator.getNoteFromMIDI((double)midiNote);
		if(ignoreRecordingNoise && (note.octave < 3 || note.octave > 7))
		{
			return;
		}

		if(lastNote == null)
		{
			// We don't know the note's type yet until we know when the next note occurs.
			lastNote = note;
		}
		else
		{
			// We can now determine what the last recorded note's type is.
			// Thus, we can now display it.
			int bpm = getBPM();
			lastNote.type = getNoteType(bpm);	
			staffLayout.addNote(lastNote);
			lastNote = note;
			
			TextView noteRecorded = (TextView)getActivity().findViewById(R.id.fragment_recording_note_recorded_textview);
			noteRecorded.setText("Note recorded: " + note.toString());
		}
	}

	private void startPDService()
	{
		if(!pdService.isRunning())
		{
			Intent intent = new Intent(((MainActivity)getActivity()), MainActivity.class);
			pdService.startAudio(intent, R.drawable.icon, "Amadeus", "Return to Amadeus");
		}
	}

	private void loadPatch() throws IOException
	{
		File dir = getActivity().getFilesDir();
		IoUtils.extractZipResource(getResources().openRawResource(R.raw.tuner), dir, true);
		File patchFile = new File(dir, "tuner.pd");
		PdBase.openPatch(patchFile.getAbsolutePath());
	}

	private void initSystemServices()
	{
		TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener()
		{
			@Override
			public void onCallStateChanged(int state, String incomingNumber)
			{
				if(pdService == null)
				{
					return;
				}
				
				if(state == TelephonyManager.CALL_STATE_IDLE)
				{
					startPDService();
				}
				else
				{
					pdService.stopAudio();
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	private int getNoteType(float bpm)
	{
		float bps = bpm / 60.0f;
		int ms = (int)((1.0f / bps) * 1000); 
		long currTime = Calendar.getInstance().getTimeInMillis();
		float timeSinceLastNote = currTime - lastRecordedNoteTime;
		
		// Go through each different note type's duration.
		// Find the one that most closely matches timeSinceLastNote.
		float best = Float.MAX_VALUE;
		float besti = 0;
		for(float i = 4.0f; i >= .025f; i /= 2.0f)
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
	
	private boolean isPlaying()
	{
		return midiPlayer != null && midiPlayer.isPlaying();
	}
	
	private int getBPM()
	{
		return Integer.parseInt(bpmSpinner.getSelectedItem().toString());
	}
	
	public static RecordingFragment newInstance()
	{
		return new RecordingFragment();
	}
}

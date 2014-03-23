
package com.cs429.amadeus.fragments;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
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
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.R;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.helpers.NoteCalculator;
import com.cs429.amadeus.views.NoteView;
import com.cs429.amadeus.views.StaffLayout;

public class RecordingFragment extends Fragment
{
	private boolean isRecording = false;
	private boolean isPlaying = false;
	private long lastNoteTime = 0;
	private int currNoteViewIndex = 0;
	private LinkedList<NoteView> noteViews;
	private Button playStopButton; // need to be able to change its text
	private Spinner noteCooldownSpinner; // need to be able to get its selected value
	private StaffLayout staffLayout;
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
			// this method will never be called
		}
	};

	public RecordingFragment()
	{
		// Empty constructor required for fragment subclasses
	}

	public static RecordingFragment newInstance()
	{
		return new RecordingFragment();
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
		noteCooldownSpinner = (Spinner)getActivity().findViewById(R.id.fragment_recording_note_cooldown_spinner);
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
		
		final Button startStopButton = (Button)getActivity().findViewById(R.id.fragment_recording_start_stop_recording_button);
		startStopButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				String newText = isRecording ? "Start recording" : "Stop recording";
				startStopButton.setText(newText);
				
				isRecording = !isRecording;
			}			
		});
		
		playStopButton = (Button)getActivity().findViewById(R.id.fragment_recording_play_stop_notes_button);
		playStopButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				String newText = isPlaying ? "Play notes" : "Stop playing";
				playStopButton.setText(newText);
				
				isPlaying = !isPlaying;
				if(isPlaying)
				{
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
				RecordingFragment.this.staffLayout.clearAllNoteViews();
			}			
		});
	}
	
	private void playNotes()
	{
		noteViews = staffLayout.getAllNoteViews();		
		int noteCooldown = Integer.parseInt(noteCooldownSpinner.getSelectedItem().toString());
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() 
		{
			@Override
			public void run() 
			{		  
				if(currNoteViewIndex >= noteViews.size() || !isPlaying)
				{
					RecordingFragment.this.getActivity().runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							playStopButton.setText("Play notes");
						}
						
					});

					isPlaying = false;
					currNoteViewIndex = 0;
					cancel();
				}
				
				NoteView noteView = noteViews.get(currNoteViewIndex);
				Note note = noteView.getNote();
				float midiNote = (float)NoteCalculator.getMIDIFromNote(note);
				PdBase.sendFloat("midinote", midiNote);
				PdBase.sendBang("trigger");
				currNoteViewIndex++;
			  }
		}, 0, noteCooldown);
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
		// Configure the audio glue
		AudioParameters.init(getActivity());
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 1, 2, 10.0f);
		startPDService();

		// Create and install the dispatcher
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter()
		{
			@Override
			public void receiveFloat(String source, final float x)
			{
				if(!isRecording || isPlaying)
				{
					return;
				}
				
				long currTime = Calendar.getInstance().getTimeInMillis();
				int noteCooldown = Integer.parseInt(noteCooldownSpinner.getSelectedItem().toString());
				if(currTime - lastNoteTime > noteCooldown)
				{
					lastNoteTime = currTime;
					updateStaffView(x);
				}
			}
		});
	}

	private void updateStaffView(float x)
	{	
		Note note = NoteCalculator.getNoteFromMIDI((double)x);
		staffLayout.addNote(note);
		TextView noteRecorded = (TextView)getActivity().findViewById(R.id.fragment_recording_note_recorded_textview);
		noteRecorded.setText("Note recorded: " + note.toString());
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
					return;
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
}

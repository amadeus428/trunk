
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.R;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.helpers.Metronome;
import com.cs429.amadeus.helpers.NoteCalculator;
import com.cs429.amadeus.helpers.OpenSaveSheetHelper;
import com.cs429.amadeus.helpers.Recorder;
import com.cs429.amadeus.helpers.StaffMIDIPlayer;
import com.cs429.amadeus.views.StaffLayout;

public class RecordingFragment extends Fragment
{
	private ImageButton playStopNotesButton;
	private Spinner bpmSpinner;
	private StaffLayout staffLayout;
	private Recorder recorder;
	private Metronome metronome;
	private StaffMIDIPlayer midiPlayer;
	private AlertDialog.Builder openDialog;
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

		initSpinners();
		createButtonListeners();

		initSystemServices();
		getActivity().bindService(new Intent(getActivity(), PdService.class), pdConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop()
	{
		super.onDestroy();
		getActivity().unbindService(pdConnection);
	}

	private void initSpinners()
	{
		bpmSpinner = (Spinner)getActivity().findViewById(R.id.fragment_recording_bpm_spinner);

		final Spinner noteTypeSpinner = (Spinner)getActivity().findViewById(R.id.fragment_recording_note_spinner);
		noteTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, int pos, long id)
			{
				switch (pos)
				{
					case 0:
						staffLayout.setAddNoteType(Note.WHOLE_NOTE);
						break;
					case 1:
						staffLayout.setAddNoteType(Note.HALF_NOTE);
						break;
					case 2:
						staffLayout.setAddNoteType(Note.QUARTER_NOTE);
						break;
					case 3:
						staffLayout.setAddNoteType(Note.EIGHTH_NOTE);
						break;
					case 4:
						staffLayout.setAddNoteType(Note.SIXTEENTH_NOTE);
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter)
			{
			}
		});
	}

	private void createButtonListeners()
	{
		final Button openButton = (Button)getActivity().findViewById(R.id.fragment_recording_open_recording_button);
		openButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				createOpenDialog();
				openDialog.show();
			}
		});

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

		final Button startStopRecordingButton = (Button)getActivity().findViewById(
				R.id.fragment_recording_start_stop_recording_button);
		startStopRecordingButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(isPlaying())
				{
					return;
				}

				// If currently playing notes, don't allow user to record.
				if(recorder != null && recorder.isRecording())
				{
					metronome.stop();
					recorder.stop();
				}
				else
				{
					((HorizontalScrollView)staffLayout.getParent()).scrollTo(0, 0);

					recorder = new Recorder(staffLayout, getBPM())
					{
						@Override
						public void onNote(final Note note)
						{
							RecordingFragment.this.getActivity().runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									TextView noteRecorded = (TextView)getActivity().findViewById(
											R.id.fragment_recording_note_recorded_textview);
									noteRecorded.setText("Note recorded: " + note.toString());
								}
							});
						}
					};
					recorder.start();

					final TextView noteRecordedTextView = (TextView)getActivity().findViewById(
							R.id.fragment_recording_note_recorded_textview);
					metronome = new Metronome(getActivity(), getBPM())
					{
						@Override
						public void onTickStart()
						{
							noteRecordedTextView.setBackgroundColor(Color.RED);
						}

						@Override
						public void onTickEnd()
						{
							noteRecordedTextView.setBackgroundColor(Color.TRANSPARENT);

						}
					};
					metronome.start();
				}
				String newText = recorder.isRecording() ? "Stop" : "Record";
				startStopRecordingButton.setText(newText);
			}
		});

		playStopNotesButton = (ImageButton)getActivity().findViewById(R.id.fragment_recording_play_stop_notes_button);
		playStopNotesButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(recorder != null && recorder.isRecording())
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
					((HorizontalScrollView)staffLayout.getParent()).scrollTo(0, 0);
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
				// If playing or recording notes, don't allow user to clear the
				// notes.
				if(!isPlaying() && (recorder == null || !recorder.isRecording()))
				{
					RecordingFragment.this.staffLayout.clearAllNoteViews();
					TextView noteRecorded = (TextView)getActivity().findViewById(
							R.id.fragment_recording_note_recorded_textview);
					noteRecorded.setText("Note recorded: ");
				}
			}
		});
	}

	private void playNotes()
	{
		// While playing notes, disable touch events on the staff.
		staffLayout.setEnabled(false);

		midiPlayer = new StaffMIDIPlayer(getActivity(), staffLayout, getBPM())
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
		saveDialog.setTitle("Save recording").setMessage("Choose save filename").setView(input).setPositiveButton("Ok",
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int whichButton)
					{
						String filename = ((EditText)input).getText().toString();
						if(filename != null && filename.length() > 0)
						{
							OpenSaveSheetHelper.saveSheet(getActivity(), filename, staffLayout);
						}
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dialog.cancel();
			}
		});
	}

	private void createOpenDialog()
	{
		File recordingsRoot = getActivity().getDir("recordings", Context.MODE_PRIVATE);
		final String[] filenames = recordingsRoot.list();

		openDialog = new AlertDialog.Builder(getActivity());
		openDialog.setTitle("Open recording").setItems(filenames, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String filename = filenames[which];
				OpenSaveSheetHelper.openSheet(getActivity(), filename, staffLayout);
				dialog.dismiss();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
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
				if(recorder != null)
				{
					recorder.tryRecordFloat(midiNote, false);
				}
			}
		});
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

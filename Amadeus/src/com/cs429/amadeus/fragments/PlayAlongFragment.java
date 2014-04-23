package com.cs429.amadeus.fragments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.R;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.helpers.Metronome;
import com.cs429.amadeus.helpers.OpenSaveSheetHelper;
import com.cs429.amadeus.helpers.PlayAlongAnalyzer;
import com.cs429.amadeus.helpers.PlayAlongStaffMIDIPlayer;
import com.cs429.amadeus.helpers.Recorder;
import com.cs429.amadeus.helpers.StaffMIDIPlayer;
import com.cs429.amadeus.views.NoteView;
import com.cs429.amadeus.views.PlayAlongStaffLayout;
import com.cs429.amadeus.views.StaffLayout;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Handles playing along to a pre-transcribed song
 */
public class PlayAlongFragment extends Fragment {
	private ImageButton playStopNotesButton;
	private Spinner bpmSpinner;
	private PlayAlongStaffLayout staffLayout;
	private Recorder recorder;
	private Metronome metronome;
	private PlayAlongStaffMIDIPlayer midiPlayer;
	private AlertDialog.Builder openDialog;
	private AlertDialog.Builder scoreDialog;
	private PdUiDispatcher dispatcher;
	private PdService pdService = null;

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder) service).getService();
			try {
				initPd();
				loadPatch();
			} catch (IOException e) {
				Log.e("TAG", e.toString());
				getActivity().finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// This method will never be called.
		}
	};

	public PlayAlongFragment() {
		// Empty constructor required for fragment subclasses.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_play_along, container, false);
	}

	/**
	 * Creates different menu options
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("Play Along");

		staffLayout = (PlayAlongStaffLayout) getActivity().findViewById(
				R.id.fragment_play_along_staff_layout);

		bpmSpinner = (Spinner) getActivity().findViewById(
				R.id.fragment_play_along_bpm_spinner);
		createButtonListeners();

		initSystemServices();
		getActivity().bindService(new Intent(getActivity(), PdService.class),
				pdConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * Called when the stop button is pressed. Stops recording the user input.
	 */
	@Override
	public void onStop() {
		super.onDestroy();
		getActivity().unbindService(pdConnection);
	}

	private void createButtonListeners() {
		final Button openButton = (Button) getActivity().findViewById(
				R.id.fragment_play_along_open_recording_button);
		openButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createOpenDialog();
				openDialog.show();
			}
		});

		final Button startStopButton = (Button) getActivity().findViewById(
				R.id.fragment_play_along_start_stop_button);
		startStopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPlaying()) {
					return;
				}

				if (recorder != null && recorder.isRecording()) {
					metronome.stop();
					recorder.stop();

					createScoreDialog();
					scoreDialog.show();
				} else {
					((HorizontalScrollView) staffLayout.getParent()).scrollTo(
							0, 0);
					staffLayout.clearAllRecordedNoteViews();

					recorder = new Recorder(staffLayout, getBPM()) {
						// Called when a note is read
						@Override
						public void onNote(final Note note) {
							PlayAlongFragment.this.getActivity().runOnUiThread(
									new Runnable() {
										@Override
										public void run() {
											TextView noteRecorded = (TextView) getActivity()
													.findViewById(
															R.id.fragment_play_along_note_recorded_textview);
											noteRecorded
													.setText("Note recorded: "
															+ note.toString());
										}
									});
						}
					};
					recorder.start();

					final TextView noteRecordedTextView = (TextView) getActivity()
							.findViewById(
									R.id.fragment_play_along_note_recorded_textview);
					metronome = new Metronome(getActivity(), getBPM()) {
						@Override
						public void onTickStart() {
							noteRecordedTextView.setBackgroundColor(Color.RED);
						}

						@Override
						public void onTickEnd() {
							noteRecordedTextView
									.setBackgroundColor(Color.TRANSPARENT);
						}
					};
					metronome.start();
				}
				String newText = recorder.isRecording() ? "Stop" : "Start";
				startStopButton.setText(newText);
			}
		});

		playStopNotesButton = (ImageButton) getActivity().findViewById(
				R.id.fragment_play_along_play_stop_notes_button);
		playStopNotesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (recorder != null && recorder.isRecording()) {
					return;
				}

				if (isPlaying()) {
					playStopNotesButton.setImageResource(R.drawable.play);
					midiPlayer.stop();
				} else {
					playStopNotesButton.setImageResource(R.drawable.stop);
					((HorizontalScrollView) staffLayout.getParent()).scrollTo(
							0, 0);
					playNotes();
				}
			}
		});
	}

	private void playNotes() {
		midiPlayer = new PlayAlongStaffMIDIPlayer(getActivity(), staffLayout,
				getBPM()) {
			@Override
			public void onFinished() {
				PlayAlongFragment.this.getActivity().runOnUiThread(
						new Runnable() {
							@Override
							public void run() {
								playStopNotesButton
										.setImageResource(R.drawable.play);
							}

						});
			}
		};
		midiPlayer.play();
	}

	private void createOpenDialog() {
		File recordingsRoot = getActivity().getDir("recordings",
				Context.MODE_PRIVATE);
		final String[] filenames = recordingsRoot.list();

		openDialog = new AlertDialog.Builder(getActivity());
		openDialog
				.setTitle("Open recording")
				.setItems(filenames, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String filename = filenames[which];
						OpenSaveSheetHelper.openSheet(getActivity(), filename,
								staffLayout);
						dialog.dismiss();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});
	}

	private void createScoreDialog() {
		float score = PlayAlongAnalyzer.getPlayAlongScore(staffLayout);
		scoreDialog = new AlertDialog.Builder(getActivity());
		scoreDialog
				.setTitle("Grade")
				.setMessage("You scored " + score + "%")
				.setPositiveButton("Sweet!",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});
	}

	private void initPd() throws IOException {
		// Configure the audio glue.
		AudioParameters.init(getActivity());
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 1, 2, 10.0f);
		startPDService();

		// Create and install the dispatcher.
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, final float midiNote) {
				if (recorder != null) {
					recorder.tryRecordFloat(midiNote, true);
				}
			}
		});
	}

	private void startPDService() {
		if (!pdService.isRunning()) {
			Intent intent = new Intent(((MainActivity) getActivity()),
					MainActivity.class);
			pdService.startAudio(intent, R.drawable.icon, "Amadeus",
					"Return to Amadeus");
		}
	}

	private void loadPatch() throws IOException {
		File dir = getActivity().getFilesDir();
		IoUtils.extractZipResource(getResources().openRawResource(R.raw.tuner),
				dir, true);
		File patchFile = new File(dir, "tuner.pd");
		PdBase.openPatch(patchFile.getAbsolutePath());
	}

	private void initSystemServices() {
		TelephonyManager telephonyManager = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (pdService == null) {
					return;
				}

				if (state == TelephonyManager.CALL_STATE_IDLE) {
					startPDService();
				} else {
					pdService.stopAudio();
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private boolean isPlaying() {
		return midiPlayer != null && midiPlayer.isPlaying();
	}

	private int getBPM() {
		return Integer.parseInt(bpmSpinner.getSelectedItem().toString());
	}

	public static PlayAlongFragment newInstance() {
		return new PlayAlongFragment();
	}
}

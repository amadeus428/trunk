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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.R;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.helpers.Metronome;
import com.cs429.amadeus.helpers.NoteCalculator;
import com.cs429.amadeus.helpers.OpenSaveHelper;
import com.cs429.amadeus.helpers.Recorder;
import com.cs429.amadeus.helpers.SoundProfile;
import com.cs429.amadeus.helpers.StaffMIDIPlayer;
import com.cs429.amadeus.views.StaffLayout;

/**
 * Handles transcribing user input.
 */
public class RecordingFragment extends Fragment {
    private float bpm;
    private ImageButton playStopNotesButton;
    private StaffLayout staffLayout;
    private Recorder recorder;
    private Metronome metronome;
    private StaffMIDIPlayer midiPlayer;
    private SoundProfile soundProfile;
    private AlertDialog.Builder openDialog;
    private AlertDialog.Builder saveDialog;
    private AlertDialog.Builder deleteDialog;
    private AlertDialog.Builder bpmDialog;
    private AlertDialog.Builder addNoteDialog;
    private AlertDialog.Builder settingsDialog;
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

    public RecordingFragment() {
	// Empty constructor required for fragment subclasses.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	return inflater.inflate(R.layout.fragment_recording, container, false);
    }

    /**
     * Sets up the buttons on the screen
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	getActivity().setTitle("Record");

	staffLayout = (StaffLayout) getActivity().findViewById(
		R.id.fragment_recording_staff_layout);

	createButtonListeners();

	initSystemServices();
	getActivity().bindService(new Intent(getActivity(), PdService.class),
		pdConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
	super.onDestroy();
	getActivity().unbindService(pdConnection);
    }

    private void createButtonListeners() {
	final Button fileButton = (Button) getActivity().findViewById(
		R.id.fragment_recording_file_button);
	fileButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		PopupMenu popup = new PopupMenu(getActivity(), v);
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		    @Override
		    public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.fragment_recording_file_menu_open:
			    createOpenDialog();
			    openDialog.show();
			    break;
			case R.id.fragment_recording_file_menu_save:
			    createSaveDialog();
			    saveDialog.show();
			    break;
			case R.id.fragment_recording_file_menu_delete:
			    createDeleteDialog();
			    deleteDialog.show();
			    break;
			case R.id.fragment_recording_file_menu_settings:
			    createSettingsDialog();
			    settingsDialog.show();
			    break;
			}

			return true;
		    }
		});

		MenuInflater menuInflater = popup.getMenuInflater();
		menuInflater.inflate(R.layout.fragment_recording_file_menu,
			popup.getMenu());
		popup.show();
	    }
	});

	final ImageButton startStopRecordingButton = (ImageButton) getActivity()
		.findViewById(
			R.id.fragment_recording_start_stop_recording_button);
	startStopRecordingButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		if (isPlaying()) {
		    return;
		}

		// If currently playing notes, don't allow user to record.
		if (recorder != null && recorder.isRecording()) {
		    metronome.stop();
		    recorder.stop();
		    flipRecordButtonImage();
		} else {
		    createBPMDialog(true);
		    bpmDialog.show();
		}
	    }
	});

	playStopNotesButton = (ImageButton) getActivity().findViewById(
		R.id.fragment_recording_play_stop_notes_button);
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
		    if (staffLayout.getChildCount() > 0) {
			createBPMDialog(false);
			bpmDialog.show();
		    }
		}
	    }
	});

	final ImageButton addNoteButton = (ImageButton) getActivity()
		.findViewById(R.id.fragment_recording_add_note_button);
	addNoteButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		createAddNoteDialog();
		addNoteDialog.show();
	    }
	});

	final Button clearNotesButton = (Button) getActivity().findViewById(
		R.id.fragment_recording_clear_notes_button);
	clearNotesButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// If playing or recording notes, don't allow user to clear the
		// notes.
		if (!isPlaying()
			&& (recorder == null || !recorder.isRecording())) {
		    RecordingFragment.this.staffLayout.clearAllNoteViews();
		    TextView noteRecorded = (TextView) getActivity()
			    .findViewById(
				    R.id.fragment_recording_note_recorded_textview);
		    noteRecorded.setText("Note recorded: ");
		}
	    }
	});
    }

    private void playNotes() {
	// While playing notes, disable touch events on the staff.
	staffLayout.setEnabled(false);

	midiPlayer = new StaffMIDIPlayer(getActivity(), staffLayout,
		soundProfile, bpm) {
	    @Override
	    public void onFinished() {
		RecordingFragment.this.getActivity().runOnUiThread(
			new Runnable() {
			    @Override
			    public void run() {
				playStopNotesButton
					.setImageResource(R.drawable.play);

				// Re-enable touch events on the staff.
				staffLayout.setEnabled(true);
			    }

			});
	    }
	};
	midiPlayer.play();
    }

    private void createSaveDialog() {
	final EditText input = new EditText(getActivity());
	saveDialog = new AlertDialog.Builder(getActivity());
	saveDialog
		.setTitle("Save recording")
		.setMessage("Choose save filename")
		.setView(input)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int whichButton) {
			String filename = ((EditText) input).getText()
				.toString();
			if (filename != null && filename.length() > 0) {
			    OpenSaveHelper.saveSheet(getActivity(), filename,
				    staffLayout);
			}
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

    private void createOpenDialog() {
	File recordingsRoot = getActivity().getDir(
		OpenSaveHelper.RECORDINGS_DIR, Context.MODE_PRIVATE);
	final String[] filenames = recordingsRoot.list();

	openDialog = new AlertDialog.Builder(getActivity());
	openDialog
		.setTitle("Open recording")
		.setItems(filenames, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			String filename = filenames[which];
			OpenSaveHelper.openSheet(getActivity(), filename,
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

    private void createDeleteDialog() {
	File recordingsRoot = getActivity().getDir(
		OpenSaveHelper.RECORDINGS_DIR, Context.MODE_PRIVATE);
	final String[] filenames = recordingsRoot.list();

	deleteDialog = new AlertDialog.Builder(getActivity());
	deleteDialog
		.setTitle("Delete recording")
		.setItems(filenames, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, final int which1) {
			AlertDialog.Builder areYouSureDialog = new AlertDialog.Builder(
				getActivity());
			areYouSureDialog
				.setTitle("Are you sure?")
				.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(
						    DialogInterface dialog,
						    int which2) {
						String filename = filenames[which1];
						File rootDir = getActivity()
							.getDir(OpenSaveHelper.RECORDINGS_DIR,
								Context.MODE_PRIVATE);
						File file = new File(rootDir,
							filename);
						file.delete();

						dialog.dismiss();
					    }
					})
				.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(
						    DialogInterface dialog,
						    int which) {
						dialog.cancel();
					    }
					}).show();
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

    private void createBPMDialog(final boolean fromRecord) {
	final EditText input = new EditText(getActivity());
	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	bpmDialog = new AlertDialog.Builder(getActivity());
	bpmDialog
		.setTitle("Enter BPM")
		.setMessage("Enter the beats per minute")
		.setView(input)
		.setPositiveButton("Cool!",
			new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog,
				    int whichButton) {
				String bpm = ((EditText) input).getText()
					.toString();
				if (bpm != null && bpm.length() > 0) {
				    RecordingFragment.this.bpm = Float
					    .parseFloat(bpm);
				}

				if (fromRecord) {
				    startRecording();
				} else {
				    playStopNotesButton
					    .setImageResource(R.drawable.stop);
				    ((HorizontalScrollView) staffLayout
					    .getParent()).scrollTo(0, 0);
				    playNotes();
				}
			    }
			});
    }

    private void createAddNoteDialog() {
	final LinearLayout view = (LinearLayout) getActivity()
		.getLayoutInflater().inflate(
			R.layout.fragment_recording_add_note_dialog, null);
	final Spinner spinner = (Spinner) view
		.findViewById(R.id.fragment_recording_add_note_dialog_note_spinner);
	final CheckBox sharpCheckBox = (CheckBox) view
		.findViewById(R.id.fragment_recording_add_note_dialog_sharp_check_box);

	addNoteDialog = new AlertDialog.Builder(getActivity());
	addNoteDialog
		.setTitle("Select add note type")
		.setView(view)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int whichButton) {
			int spinnerItemPos = spinner.getSelectedItemPosition();
			boolean sharpChecked = sharpCheckBox.isChecked();
			setAddNoteType(spinnerItemPos, sharpChecked);
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

    private void createSettingsDialog() {
	File profilesRoot = getActivity().getDir(
		OpenSaveHelper.SOUND_PROFILES_DIR, Context.MODE_PRIVATE);
	final String[] filenames = profilesRoot.list();

	settingsDialog = new AlertDialog.Builder(getActivity());
	settingsDialog
		.setTitle("Open sound profile")
		.setItems(filenames, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			String fileName = filenames[which];
			soundProfile = OpenSaveHelper.openSoundProfile(
				getActivity(), fileName);
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

    private void setAddNoteType(int spinnerPos, boolean sharp) {
	switch (spinnerPos) {
	case 0:
	    staffLayout.setAddNoteType(Note.WHOLE_NOTE, sharp);
	    break;
	case 1:
	    staffLayout.setAddNoteType(Note.HALF_NOTE, sharp);
	    break;
	case 2:
	    staffLayout.setAddNoteType(Note.QUARTER_NOTE, sharp);
	    break;
	case 3:
	    staffLayout.setAddNoteType(Note.EIGHTH_NOTE, sharp);
	    break;
	case 4:
	    staffLayout.setAddNoteType(Note.SIXTEENTH_NOTE, sharp);
	    break;
	}
    }

    private void startRecording() {
	((HorizontalScrollView) staffLayout.getParent()).scrollTo(0, 0);

	recorder = new Recorder(staffLayout, bpm) {
	    @Override
	    public void onNote(final Note note) {
		getActivity().runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
			TextView noteRecorded = (TextView) getActivity()
				.findViewById(
					R.id.fragment_recording_note_recorded_textview);
			noteRecorded.setText("Note recorded: "
				+ note.toString());
		    }
		});
	    }
	};
	recorder.start();

	final TextView noteRecordedTextView = (TextView) getActivity()
		.findViewById(R.id.fragment_recording_note_recorded_textview);
	metronome = new Metronome(getActivity(), bpm) {
	    @Override
	    public void onTickStart() {
		noteRecordedTextView.setBackgroundColor(Color.RED);
	    }

	    @Override
	    public void onTickEnd() {
		noteRecordedTextView.setBackgroundColor(Color.TRANSPARENT);

	    }
	};
	metronome.start();

	flipRecordButtonImage();
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
		    recorder.tryRecordFloat(midiNote, false);
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

    private void flipRecordButtonImage() {
	final ImageButton startStopRecordingButton = (ImageButton) getActivity()
		.findViewById(
			R.id.fragment_recording_start_stop_recording_button);
	int srcId = recorder.isRecording() ? R.drawable.record_on
		: R.drawable.record_off;
	startStopRecordingButton.setImageResource(srcId);
    }

    private boolean isPlaying() {
	return midiPlayer != null && midiPlayer.isPlaying();
    }

    public static RecordingFragment newInstance() {
	return new RecordingFragment();
    }
}

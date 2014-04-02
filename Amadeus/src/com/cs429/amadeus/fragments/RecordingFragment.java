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
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.R;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.helpers.Metronome;
import com.cs429.amadeus.helpers.NoteCalculator;
import com.cs429.amadeus.helpers.Recorder;
import com.cs429.amadeus.helpers.StaffMIDIPlayer;
import com.cs429.amadeus.views.StaffLayout;

public class RecordingFragment extends Fragment {

    private ImageButton playStopNotesButton; // need to be able to change its
					     // text
    private Spinner bpmSpinner; // need to be able to get its selected value
    private StaffLayout staffLayout;
    private Recorder recorder;
    private Metronome metronome;
    private StaffMIDIPlayer midiPlayer;
    private AlertDialog.Builder saveDialog;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	getActivity().setTitle("Record");

	staffLayout = (StaffLayout) getActivity().findViewById(
		R.id.fragment_recording_staff_layout);
	bpmSpinner = (Spinner) getActivity().findViewById(
		R.id.fragment_recording_bpm_spinner);
	ArrayAdapter<CharSequence> bpmSpinnerAdapter = ArrayAdapter
		.createFromResource(getActivity(), R.array.bpm_items,
			R.drawable.spinner_item);
	bpmSpinnerAdapter.setDropDownViewResource(R.drawable.spinner_item);
	bpmSpinner.setAdapter(bpmSpinnerAdapter);
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
	final Button saveButton = (Button) getActivity().findViewById(
		R.id.fragment_recording_save_recording_button);
	saveButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View view) {
		createSaveDialog();
		saveDialog.show();
	    }
	});

	final Button startStopRecordingButton = (Button) getActivity()
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
		} else {
		    ((HorizontalScrollView) staffLayout.getParent()).scrollTo(
			    0, 0);

		    recorder = new Recorder(staffLayout, getBPM()) {
			@Override
			public void onNote(final Note note) {
			    RecordingFragment.this.getActivity().runOnUiThread(
				    new Runnable() {
					@Override
					public void run() {
					    TextView noteRecorded = (TextView) getActivity()
						    .findViewById(
							    R.id.fragment_recording_note_recorded_textview);
					    noteRecorded
						    .setText("Note recorded: "
							    + note.toString());
					}
				    });
			}
		    };
		    recorder.start();

		    metronome = new Metronome(getActivity(), getBPM());
		    metronome.start();
		}
		String newText = recorder.isRecording() ? "Stop" : "Record";
		startStopRecordingButton.setText(newText);
	    }
	});

	playStopNotesButton = (ImageButton) getActivity().findViewById(
		R.id.fragment_recording_play_stop_notes_button);
	playStopNotesButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		if (recorder.isRecording()) {
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

	final Button clearNotesButton = (Button) getActivity().findViewById(
		R.id.fragment_recording_clear_notes_button);
	clearNotesButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// If playing or recording notes, don't allow user to clear the
		// notes.
		if (!isPlaying() && !recorder.isRecording()) {
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

	midiPlayer = new StaffMIDIPlayer(getActivity(), staffLayout, getBPM()) {
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
		    recorder.tryRecordFloat(midiNote);
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

    public static RecordingFragment newInstance() {
	return new RecordingFragment();
    }
}

package com.cs429.amadeus;

import java.io.File;
import java.io.IOException;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int RECORDER_SAMPLE_RATE = 22050;
	private static final int RECORDER_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final int RECORDER_BUFFER_SIZE = 1024;

	private NoteCalculator noteCalculator;
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
				finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.staff_example);
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_view);
		frame.addView(new StaffView(this));
		/*
		 * setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);
		 * 
		 * @Override protected void onCreate(Bundle savedInstanceState) {
		 * super.onCreate(savedInstanceState);
		 * setContentView(R.layout.activity_main);
		 * setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);
		 * 
		 * AudioManager audioManager = (AudioManager)
		 * getSystemService(Context.AUDIO_SERVICE);
		 * audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		 * audioManager.setSpeakerphoneOn(true);
		 * 
		 * // Set up code for frequency to note demo noteCalculator = new
		 * NoteCalculator(); setUpButtonListenerForDemo();
		 * 
		 * initSystemServices(); bindService(new Intent(this, PdService.class),
		 * pdConnection, BIND_AUTO_CREATE); }
		 * 
		 * @Override public void onDestroy() { super.onDestroy();
		 * unbindService(pdConnection); }
		 * 
		 * initSystemServices(); bindService(new Intent(this, PdService.class),
		 * pdConnection, BIND_AUTO_CREATE);
		 */
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(pdConnection);
	}

	private void initPd() throws IOException {
		// Configure the audio glue
		AudioParameters.init(this);
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 1, 2, 10.0f);
		start();

		// Create and install the dispatcher
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		final Context context = this;
		dispatcher.addListener("pitch", new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, final float x) {
				updateTextView(x);
				// Toast.makeText(context, x + "", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void updateTextView(float x) {
		TextView tv = (TextView) findViewById(R.id.pd_textView);
		tv.setText(x + "");
	}

	private void start() {
		if (!pdService.isRunning()) {
			Intent intent = new Intent(MainActivity.this, MainActivity.class);
			pdService.startAudio(intent, R.drawable.icon, "GuitarTuner",
					"Return to GuitarTuner.");
		}
	}

	private void loadPatch() throws IOException {
		File dir = getFilesDir();
		IoUtils.extractZipResource(getResources().openRawResource(R.raw.tuner),
				dir, true);
		File patchFile = new File(dir, "tuner.pd");
		PdBase.openPatch(patchFile.getAbsolutePath());
	}

	private void initSystemServices() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (pdService == null)
					return;
				if (state == TelephonyManager.CALL_STATE_IDLE) {
					start();
				} else {
					pdService.stopAudio();
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void setUpButtonListenerForDemo() {
		Button button = (Button) findViewById(R.id.calculate_button);
		final Context context = this;
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText frequencyText = (EditText) findViewById(R.id.frequency_editText);

				if (frequencyText.getText().toString().isEmpty()) {
					Toast.makeText(context, "Enter a number",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String frequency = frequencyText.getText().toString();
				double frequencyAsDouble = (double) Double
						.parseDouble(frequency);
				String note = noteCalculator.calculateNote(frequencyAsDouble);
				TextView noteTextView = (TextView) findViewById(R.id.note_textView);
				noteTextView.setText(note);
			}
		});
	}

}

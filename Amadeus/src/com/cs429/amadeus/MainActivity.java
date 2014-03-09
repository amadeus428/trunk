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

	private AudioRecord recorder;
	private AudioTrack track;
	private boolean isPlaying = false;
	private byte[] recorderBuffer = new byte[RECORDER_BUFFER_SIZE];
	private NoteCalculator noteCalculator;
	
	private PdUiDispatcher dispatcher;
	private PdService pdService = null;
	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				initPd();
				loadPatch();
			} catch (IOException e) {
				Log.e("TAG", e.toString());
				finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.staff_example);
		FrameLayout frame = (FrameLayout)findViewById(R.id.frame_view);
		frame.addView(new StaffView(this));
		/*setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		audioManager.setSpeakerphoneOn(true);

		int min = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE,
				RECORDER_CHANNEL, RECORDER_AUDIO_ENCODING);
		recorder = new AudioRecord(
				MediaRecorder.AudioSource.VOICE_COMMUNICATION,
				RECORDER_SAMPLE_RATE, RECORDER_CHANNEL,
				RECORDER_AUDIO_ENCODING, min);

		int maxJitter = AudioTrack.getMinBufferSize(RECORDER_SAMPLE_RATE,
				AudioFormat.CHANNEL_OUT_MONO, RECORDER_AUDIO_ENCODING);
		track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION,
				RECORDER_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
				RECORDER_AUDIO_ENCODING, maxJitter, AudioTrack.MODE_STREAM);

		// set up code for frequency to note demo
		noteCalculator = new NoteCalculator();
		setUpButtonListenerForDemo();

		Thread thread = new Thread(new Runnable() {
			public void run() {
				record();
			}
		});
		//thread.start();
		

		initSystemServices();
		bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);
		*/
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(pdConnection);
	}
	
	private void  initPd() throws IOException {
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
				//Toast.makeText(context, x + "", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void updateTextView(float x) {
		TextView tv = (TextView) findViewById(R.id.pd_textView);
		tv.setText(x + "");
	}
	
	private void start() {
		if (!pdService.isRunning()) {
			Intent intent = new Intent(MainActivity.this,
					MainActivity.class);
			pdService.startAudio(intent, R.drawable.icon,
					"GuitarTuner", "Return to GuitarTuner.");
		}
	}
	
	private void loadPatch() throws IOException {
		File dir = getFilesDir();
		IoUtils.extractZipResource(
				getResources().openRawResource(R.raw.tuner), dir, true);
		File patchFile = new File(dir, "tuner.pd");
		PdBase.openPatch(patchFile.getAbsolutePath());
	}

	private void initSystemServices() {
		TelephonyManager telephonyManager =
				(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (pdService == null) return;
				if (state == TelephonyManager.CALL_STATE_IDLE) {
					start(); } else {
						pdService.stopAudio(); }
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void record() {
		recorder.startRecording();

		while (true) {
			int numBytesRead = recorder.read(recorderBuffer, 0,
					RECORDER_BUFFER_SIZE);
			track.write(recorderBuffer, 0, numBytesRead);
		}
	}

	public void pauseOrPlay(View view) {
		Button playButton = (Button) findViewById(R.id.play_btn);

		if (isPlaying) {
			recorder.stop();
			track.pause();
			isPlaying = false;
			playButton.setText("Play");
		} else {
			recorder.startRecording();
			track.play();
			isPlaying = true;
			playButton.setText("Pause");
		}
	}
	



	private double calculateFrequency(byte[] audioBuffer) {
		int bufferLen = audioBuffer.length;

		double[] micBufferData = new double[audioBuffer.length];
		final int bytesPerSample = 2;
		final double amplification = 100.0;
		for (int index = 0, floatIndex = 0; index < bufferLen - bytesPerSample
				+ 1; index += bytesPerSample, floatIndex++) {
			double sample = 0;
			for (int b = 0; b < bytesPerSample; b++) {
				int v = audioBuffer[index + b];
				if (b < bytesPerSample - 1 || bytesPerSample == 1) {
					v &= 0xFF;
				}
				sample += v << (b * 8);
			}

			double sample32 = amplification * (sample / 32768.0);
			micBufferData[floatIndex] = sample32;
		}

		Complex[] fftTempArray = new Complex[bufferLen];
		for (int i = 0; i < bufferLen; i++) {
			fftTempArray[i] = new Complex(micBufferData[i], 0);
		}

		Complex[] fftResults = FFT.fft(fftTempArray);
		double[] frequencies = new double[fftResults.length];
		for (int i = 0; i < fftResults.length; i++) {
			frequencies[i] = ((1.0 * RECORDER_SAMPLE_RATE) / (1.0 * 900)) * i;
			if (frequencies[i] > 1) {
				return frequencies[i];
			}
		}

		return 0;
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

package com.cs429.amadeus;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);

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
	thread.start();
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

	    double frequency = calculateFrequency(recorderBuffer);
	    TextView frequencyMessage = (TextView) findViewById(R.id.frequency_message);
	    frequencyMessage.setText("frequency: " + frequency);
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

package com.cs429.amadeus.test;

import java.io.File;
import java.util.Calendar;
import java.util.Random;

import com.cs429.amadeus.*;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

public class RecordingTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mActivity;
	
	private Button mStartStopButton;
	private Button mPlaybackButton;
	private EditText mFileNameField;
	public RecordingTest() {
		super(MainActivity.class);
	}
	@Override
	public void setUp() throws Exception{
		super.setUp();
		
	    setActivityInitialTouchMode(false);

	    mActivity = getActivity();


		mPlaybackButton = (Button)mActivity.findViewById(com.cs429.amadeus.R.id.playback);
		mStartStopButton = (Button)mActivity.findViewById(com.cs429.amadeus.R.id.startstop);
		mFileNameField = (EditText)mActivity.findViewById(com.cs429.amadeus.R.id.fileNameField);
	}
	
	public void testTextFieldNotEmpty() throws Exception{
		
		assertTrue(mFileNameField.getText().toString() != null);

		assertTrue(! mFileNameField.getText().toString().equals(""));
		
	}
	public void testRecordMakesFile() throws Exception{
		
		
		
		//Get a random string to be used as a file name
		Random rand = new Random(Calendar.getInstance().get(Calendar.SECOND));
		
		String randomFileName = "";
		for(int i = 0; i< 5; i++)
			randomFileName+=(char)(rand.nextInt(26)+'A');
		
		mFileNameField.setText(randomFileName);
		
		//Simulate clicking the button
		
		mStartStopButton.performClick();

		mStartStopButton.performClick();

		File file = mActivity.getFileStreamPath(randomFileName);
		assertTrue(file.exists());
		
	}
}

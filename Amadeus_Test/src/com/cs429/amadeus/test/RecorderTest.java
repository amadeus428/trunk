package com.cs429.amadeus.test;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.helpers.Recorder;

import junit.framework.TestCase;

import junit.*;

public class RecorderTest extends TestCase {

    // Test divide by zero

    public void testException() {
	try {
	    Recorder recorder = new Recorder(null, 0) {
		@Override
		public void onNote(final Note note) {

		}
	    };

	} catch (IllegalArgumentException e) {
	    assertEquals(e.getMessage(), "bps is zero");
	}

    }

    public void testBPs() {
	Recorder recorder = new Recorder(null, 500) {
	    @Override
	    public void onNote(final Note note) {

	    }
	};

	float bps = recorder.getBPS();
	float req = (float) (25.0 / 3);
	assertEquals(bps, req);
    }

    public void testPeriod() {
	Recorder recorder = new Recorder(null, 500) {
	    @Override
	    public void onNote(final Note note) {

	    }
	};

	float period = recorder.getPeriod();
	float req = (float) (30.0);
	assertEquals(period, req);
    }

    public void testRecordingBeforeInit() {
	Recorder recorder = new Recorder(null, 500) {
	    @Override
	    public void onNote(final Note note) {

	    }
	};
	assertFalse(recorder.isRecording());
    }

    public void testStarted() {
	Recorder recorder = new Recorder(null, 500) {
	    @Override
	    public void onNote(final Note note) {

	    }
	};
	recorder.start();
	assertTrue(recorder.isRecording());
    }

    public void testStopped() {
	Recorder recorder = new Recorder(null, 500) {
	    @Override
	    public void onNote(final Note note) {

	    }
	};
	recorder.start();
	recorder.stop();
	assertFalse(recorder.isRecording());
    }

}

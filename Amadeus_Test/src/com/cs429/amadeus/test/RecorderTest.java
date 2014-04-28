package com.cs429.amadeus.test;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.helpers.Recorder;

import junit.framework.TestCase;

import junit.*;


public class RecorderTest extends TestCase {
	
	
	
	/*
	 * This test case tests whether the code correctly throws
	 * a divide by zero exception given that we supplied
	 * it with an illegal input
	 */
	public void testException(){
		try{
			Recorder recorder = new Recorder(null,0)
			{
				@Override
				public void onNote(final Note note) {
				
				}
			};
			
		}
		catch(IllegalArgumentException e){
			assertEquals(e.getMessage(),"bps is zero");
		}
	
	}
	
	/*
	 * Tests whether the Recorder.java file correctly calculated the 
	 * BPS for the given parameters.
	 */
	public void testBPs(){
		Recorder recorder = new Recorder(null,500)
		{
			@Override
			public void onNote(final Note note) {
			
			}
		};
		
		float bps = recorder.getBPS();
		float req = (float)(25.0/3);
		assertEquals(bps,req);
	}

	
	/*
	 * Tests whether the Recorder.java file 
	 * correctly calculated the period given 
	 * the input parameters.
	 */
	public void testPeriod(){
		Recorder recorder = new Recorder(null,500)
		{
			@Override
			public void onNote(final Note note) {
			
			}
		};
		
		float period = recorder.getPeriod();
		float req = (float)(30.0);
		assertEquals(period,req);
		}

	/*
	 * Tests whether the initial value of
	 * isRecording is set correctly.
	 */
	public void testRecordingBeforeInit(){
		Recorder recorder = new Recorder(null,500)
		{
			@Override
			public void onNote(final Note note) {
			
			}
		};
		assertFalse(recorder.isRecording());
	}

	/*
	 * Tests whether the recorder understands
	 * when the recording has started.
	 */
	public void testStarted(){
		Recorder recorder = new Recorder(null,500)
		{
			@Override
			public void onNote(final Note note) {
			
			}
		};
		recorder.start();
		assertTrue(recorder.isRecording());
	}
	
	/*
	 * Tests whether the recorder understands 
	 * when the recording has ended.
	 */
	public void testStopped(){
		Recorder recorder = new Recorder(null,500)
		{
			@Override
			public void onNote(final Note note) {
			
			}
		};
		recorder.start();
		recorder.stop();
		assertFalse(recorder.isRecording());
	}
	
	
		
	}

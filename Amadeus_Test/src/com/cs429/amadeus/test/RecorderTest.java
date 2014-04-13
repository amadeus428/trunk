package com.cs429.amadeus.test;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.helpers.Recorder;

import junit.framework.TestCase;

public class RecorderTest extends TestCase {
	
	public void testBPs(){
	Recorder recorder = new Recorder(null,500)
	{
		@Override
		public void onNote(final Note note) {
		
		}
	};
	
	float bps = recorder.getBps();
	float req = (float)(25.0/3);
	assertEquals(bps,req);
	}

}

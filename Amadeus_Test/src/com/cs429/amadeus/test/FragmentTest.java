package com.cs429.amadeus.test;

import com.cs429.amadeus.fragments.GuitarChordFragment;
import com.cs429.amadeus.fragments.HomeFragment;
import com.cs429.amadeus.fragments.PlayAlongFragment;
import com.cs429.amadeus.fragments.RecordingFragment;
import com.cs429.amadeus.fragments.TabSearchFragment;

import junit.framework.TestCase;

public class FragmentTest extends TestCase {
	
	public void testGuitarChordFragment(){
		assertNotNull(GuitarChordFragment.newInstance());
	}
	
	public void testHomeFragment(){
		assertNotNull(HomeFragment.newInstance());
	}
	
	public void testPlayAlongFragment(){
		assertNotNull(PlayAlongFragment.newInstance());
	}
	
	public void testRecordingFragment(){
		assertNotNull(RecordingFragment.newInstance());
	}
	
	public void testTabSearchFragment(){
		assertNotNull(TabSearchFragment.newInstance());
	}
	
	
	

}

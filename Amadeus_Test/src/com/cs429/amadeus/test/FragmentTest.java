package com.cs429.amadeus.test;

import com.cs429.amadeus.fragments.GuitarChordFragment;
import com.cs429.amadeus.fragments.HomeFragment;
import com.cs429.amadeus.fragments.PlayAlongFragment;
import com.cs429.amadeus.fragments.RecordingFragment;
import com.cs429.amadeus.fragments.TabSearchFragment;

import junit.framework.TestCase;

public class FragmentTest extends TestCase {
<<<<<<< HEAD

    public void testGuitarChordFragment() {
	assertNotNull(GuitarChordFragment.newInstance());
    }

    public void testHomeFragment() {
	assertNotNull(HomeFragment.newInstance());
    }

    public void testPlayAlongFragment() {
	assertNotNull(PlayAlongFragment.newInstance());
    }

    public void testRecordingFragment() {
	assertNotNull(RecordingFragment.newInstance());
    }

    public void testTabSearchFragment() {
	assertNotNull(TabSearchFragment.newInstance());
    }
=======
	/*
	 * Tests if each fragment returns null(which would indicate an error)
	 * from the newInstance() method.
	 */
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
	
	
	
>>>>>>> c51165a385b7a4040da4f8b5a54367542f4ddd82

}

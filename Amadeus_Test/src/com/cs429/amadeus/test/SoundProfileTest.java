package com.cs429.amadeus.test;

import java.util.HashMap;
import java.util.Map.Entry;

import com.cs429.amadeus.helpers.SoundProfile;
import com.cs429.amadeus.helpers.SoundProfile.Range;

import junit.framework.TestCase;

public class SoundProfileTest extends TestCase {

    private SoundProfile profile;

    protected void setUp() throws Exception {
	super.setUp();

	profile = new SoundProfile();
    }

    public void testAddMappingNegative() {
	profile.addMapping(-1, 0, "");
	HashMap<Range, String> map = profile.getMap();
	assertEquals(map.size(), 0);
    }

    public void testAddMappingLowGreaterThanHigh() {
	profile.addMapping(100, 1, "");
	HashMap<Range, String> map = profile.getMap();
	assertEquals(map.size(), 0);
    }

    public void testAddMappingNullPath() {
	profile.addMapping(2, 4, null);
	HashMap<Range, String> map = profile.getMap();
	assertEquals(map.size(), 0);
    }

    public void testAddNormalMapping1() {
	profile.addMapping(50, 100, "path1");
	HashMap<Range, String> map = profile.getMap();
	assertEquals(map.size(), 1);

	for (Entry<Range, String> entry : map.entrySet()) {
	    Range range = entry.getKey();
	    String path = entry.getValue();

	    assertEquals(range.low, 50.0f);
	    assertEquals(range.high, 100.0f);
	    assertEquals(path, "path1");
	}
    }

    public void testAddNormalMapping2() {
	profile.addMapping(999999, 10000000, "path2");
	HashMap<Range, String> map = profile.getMap();
	assertEquals(map.size(), 1);

	for (Entry<Range, String> entry : map.entrySet()) {
	    Range range = entry.getKey();
	    String path = entry.getValue();

	    assertEquals(range.low, 999999.0f);
	    assertEquals(range.high, 10000000.0f);
	    assertEquals(path, "path2");
	}
    }
}

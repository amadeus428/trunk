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
		try {
			profile.addMapping(-1, 0, "");
			fail("Shouldn't allow mapping with negative value");
		} catch (Exception e) {
			HashMap<Range, String> map = profile.getMap();
			assertEquals(map.size(), 0);
		}
	}

	public void testAddMappingLowGreaterThanHigh() {
		try {
			profile.addMapping(100, 1, "");
			fail("Shouldn't allow mapping with low > high");
		} catch (Exception e) {
			HashMap<Range, String> map = profile.getMap();
			assertEquals(map.size(), 0);
		}
	}

	public void testAddMappingNullPath() {
		try {
			profile.addMapping(2, 4, null);
			fail("Shouldn't allow mapping with null path");
		} catch (Exception e) {
			HashMap<Range, String> map = profile.getMap();
			assertEquals(map.size(), 0);
		}
	}

	public void testAddNormalMapping1() {
		try {
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
		} catch (Exception e) {
			fail("No exception should be thrown");
		}
	}

	public void testAddNormalMapping2() {
		try {
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
		} catch (Exception e) {
			fail("No exception should be thrown");
		}
	}
}

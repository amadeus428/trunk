package com.cs429.amadeus.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.test.ActivityInstrumentationTestCase2;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.helpers.OpenSaveHelper;
import com.cs429.amadeus.helpers.SoundProfile;
import com.cs429.amadeus.views.StaffLayout;

public class OpenSaveTest extends
	ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity activity;
    private StaffLayout staff;
    private Method createSheetMethod;
    private Method createProfileMethod;

    public OpenSaveTest() {
	super(MainActivity.class);

	setActivityInitialTouchMode(false);
    }

    public void setUp() throws Exception {
	super.setUp();

	activity = getActivity();
	staff = new StaffLayout(activity);
	createSheetMethod = OpenSaveHelper.class.getDeclaredMethod("createSheetXML",
		StaffLayout.class);
	createSheetMethod.setAccessible(true);
	createProfileMethod = OpenSaveHelper.class.getDeclaredMethod("createSoundProfileXML",
		SoundProfile.class);
	createProfileMethod.setAccessible(true);
    }

    public void testEmptyStaffXML() throws NoSuchMethodException,
	    IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException {
	String xml = (String) createSheetMethod.invoke(null, staff);
	assertFalse(xml.contains("<note"));
    }

    public void testNullStaffXML() throws NoSuchMethodException,
	    IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException {
	StaffLayout testStaff = null;
	String xml = (String) createSheetMethod.invoke(null, testStaff);
	assertTrue(xml == null);
    }

    public void testEmptySoundProfileXML() throws NoSuchMethodException,
	    IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException {
	SoundProfile profile = new SoundProfile();
	String xml = (String) createProfileMethod.invoke(null, profile);
	assertFalse(xml.contains("<mapping"));
    }

    public void testNullSoundProfileXML() throws NoSuchMethodException,
	    IllegalAccessException, IllegalArgumentException,
	    InvocationTargetException {
	SoundProfile profile = null;
	String xml = (String) createProfileMethod.invoke(null, profile);
	assertTrue(xml == null);
    }
}

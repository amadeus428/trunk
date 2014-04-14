package com.cs429.amadeus.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.test.ActivityInstrumentationTestCase2;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.helpers.OpenSaveSheetHelper;
import com.cs429.amadeus.views.StaffLayout;

public class OpenSaveTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity activity;
	private StaffLayout staff;
	private Method method;

	public OpenSaveTest() {
		super(MainActivity.class);

		setActivityInitialTouchMode(false);
	}

	public void setUp() throws Exception {
		super.setUp();

		activity = getActivity();
		staff = new StaffLayout(activity);
		method = OpenSaveSheetHelper.class.getDeclaredMethod("createSheetXML",
				StaffLayout.class);
		method.setAccessible(true);
	}

	public void testEmptyStaffXML() throws NoSuchMethodException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		String xml = (String) method.invoke(null, staff);
		assertFalse(xml.contains("<note"));
	}

	public void testNullStaffXML() throws NoSuchMethodException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		StaffLayout testStaff = null;
		String xml = (String) method.invoke(null, testStaff);
		assertTrue(xml == null);
	}
}

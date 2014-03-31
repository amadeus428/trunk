package com.cs429.amadeus.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.views.StaffView;
import com.cs429.amadeus.activities.MainActivity;

public class StaffViewTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity mActivity;

	StaffView staff;

	public StaffViewTest() {
		super(MainActivity.class);
	}
	

	@Override
	public void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);

		mActivity = getActivity();
		staff = new StaffView(mActivity);

	}
	public void testGetNoteLetterFromInterval() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException{
		Class[] argumentClasses = new Class[1];
		argumentClasses[0] = Integer.class;
		Method method = staff.getClass().getDeclaredMethod("getNoteLetterFromInterval",argumentClasses);
		method.setAccessible(true);
		
		
		assertEquals(method.invoke(staff, Integer.valueOf(0)), 'F');
		assertEquals(method.invoke(staff, Integer.valueOf(3)), 'C');
		
		assertEquals(method.invoke(staff, Integer.valueOf(33)), 'A');
		assertEquals(method.invoke(staff, Integer.valueOf(-36)), 'G');
		
		
		method.setAccessible(false);
	}
	public void testGetNoteCoordinate() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Class[] argumentClasses = new Class[1];
		argumentClasses[0] = Note.class;
		Method method = staff.getClass().getDeclaredMethod("getNoteCoordinate",argumentClasses);
		method.setAccessible(true);
		
		Note note = new Note('F', 5, true, Note.QUARTER_NOTE);
		assertEquals(method.invoke(staff, note), 0);
		
		Note noteOctaveDown = new Note('F', 4, true, Note.QUARTER_NOTE);
		assertEquals(method.invoke(staff, noteOctaveDown), 7);

		Note noteLowest= new Note('A', 0, true, Note.QUARTER_NOTE);
		assertEquals(method.invoke(staff, noteLowest), 40);

		Note noteHighest= new Note('G', 10, true, Note.QUARTER_NOTE);
		assertEquals(method.invoke(staff, noteHighest), -36);

		
		method.setAccessible(false);

	}
	
	public void testGetOctave() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Class[] argumentClasses = new Class[1];
		argumentClasses[0] = Integer.class;
		Method method = staff.getClass().getDeclaredMethod("getOctave",argumentClasses);
		method.setAccessible(true);
		
		assertEquals(method.invoke(staff, 0), 5);

		assertEquals(method.invoke(staff, 5), 5);

		assertEquals(method.invoke(staff, 6), 4);

		method.setAccessible(false);

	}
}
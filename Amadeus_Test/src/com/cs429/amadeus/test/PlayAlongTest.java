package com.cs429.amadeus.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.test.ActivityInstrumentationTestCase2;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.helpers.PlayAlongAnalyzer;

import junit.framework.TestCase;

public class PlayAlongTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public PlayAlongTest() {
		super(MainActivity.class);

		setActivityInitialTouchMode(false);
	}

	public void testGetStepsDiffPenaltyPositive() throws NoSuchMethodException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = PlayAlongAnalyzer.class.getDeclaredMethod(
				"getStepsDiffPenalty", Integer.TYPE);
		method.setAccessible(true);
		float result = (Float) method.invoke(null, 1);
		assertEquals(result, 1 / 3.0f);
	}

	public void testGetStepsDiffPenaltyNegative() throws NoSuchMethodException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = PlayAlongAnalyzer.class.getDeclaredMethod(
				"getStepsDiffPenalty", Integer.TYPE);
		method.setAccessible(true);
		float result = (Float) method.invoke(null, -1);
		assertEquals(result, -1.0f);
	}

	public void testGetStepsDiffPenaltyOver3() throws NoSuchMethodException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = PlayAlongAnalyzer.class.getDeclaredMethod(
				"getStepsDiffPenalty", Integer.TYPE);
		method.setAccessible(true);
		float result = (Float) method.invoke(null, 4);
		assertEquals(result, 1.0f);
	}

	public void testGetTypeDiffNull() throws NoSuchMethodException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = PlayAlongAnalyzer.class.getDeclaredMethod(
				"getTypeDiff", Note.class, Note.class);
		method.setAccessible(true);
		int result = (Integer) method.invoke(null, null, null);
		assertEquals(result, -1);
	}

	public void testGetTypeDiffNormal() throws NoSuchMethodException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = PlayAlongAnalyzer.class.getDeclaredMethod(
				"getTypeDiff", Note.class, Note.class);
		method.setAccessible(true);
		int result = (Integer) method.invoke(null, new Note("A5",
				Note.HALF_NOTE), new Note("A5", Note.QUARTER_NOTE));
		assertEquals(result, 1);
	}
}

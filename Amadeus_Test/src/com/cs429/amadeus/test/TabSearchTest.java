package com.cs429.amadeus.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.fragments.TabSearchFragment;
import com.cs429.amadeus.R;

public class TabSearchTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity mActivity;

	TabSearchFragment tabSearch;

	public TabSearchTest() {
		super(MainActivity.class);
	}

	@SuppressLint("NewApi")
	@UiThreadTest
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.replaceContentViewOnItemSelected(mActivity
						.getIndexOfItemInDrawer(R.string.title_guitar_tabs));
			}
		});
		getInstrumentation().waitForIdleSync();
		tabSearch = (TabSearchFragment) mActivity.getFragmentManager()
				.findFragmentById(R.id.content_frame);
	}

	public void testBuildUrl() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException {
		Class[] argumentClasses = new Class[2];
		argumentClasses[0] = String.class;
		argumentClasses[1] = String.class;
		Method method = tabSearch.getClass().getDeclaredMethod("buildUrl",
				argumentClasses);
		method.setAccessible(true);

		assertEquals(method.invoke(tabSearch, "taylor swift", "fearless"),
				"http://tabs.ultimate-guitar.com/t/taylor_swift/fearless_tab.htm");

		method.setAccessible(false);
	}

	public void testBuildUrlWithExtraSpacesInArtist()
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException {
		Class[] argumentClasses = new Class[2];
		argumentClasses[0] = String.class;
		argumentClasses[1] = String.class;
		Method method = tabSearch.getClass().getDeclaredMethod("buildUrl",
				argumentClasses);
		method.setAccessible(true);

		assertEquals(method.invoke(tabSearch, "  taylor swift  ", "fearless"),
				"http://tabs.ultimate-guitar.com/t/taylor_swift/fearless_tab.htm");

		method.setAccessible(false);
	}

	public void testBuildUrlWithExtraSpacesInSong()
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException {
		Class[] argumentClasses = new Class[2];
		argumentClasses[0] = String.class;
		argumentClasses[1] = String.class;
		Method method = tabSearch.getClass().getDeclaredMethod("buildUrl",
				argumentClasses);
		method.setAccessible(true);

		assertEquals(method.invoke(tabSearch, "taylor swift", "  fearless   "),
				"http://tabs.ultimate-guitar.com/t/taylor_swift/fearless_tab.htm");

		method.setAccessible(false);
	}

	public void testBuildUrlWithNumberArtistName()
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException {
		Class[] argumentClasses = new Class[2];
		argumentClasses[0] = String.class;
		argumentClasses[1] = String.class;
		Method method = tabSearch.getClass().getDeclaredMethod("buildUrl",
				argumentClasses);
		method.setAccessible(true);

		assertEquals(method.invoke(tabSearch, "3 Doors Down", "be like that"),
				"http://tabs.ultimate-guitar.com/0-9/3_doors_down/be_like_that_tab.htm");

		method.setAccessible(false);
	}

	public void testBadHtml() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException {
		Class[] argumentClasses = new Class[1];
		argumentClasses[0] = String.class;
		Method method = tabSearch.getClass().getDeclaredMethod("getGuitarTab",
				argumentClasses);
		method.setAccessible(true);

		method.invoke(tabSearch,
				"http://tabs.ultimate-guitar.com/t/bad_artist/bad_song_tab.htm");
		String html = "";
		while ((html = tabSearch.getHtml()).equals("")) {
			;
		}
		assertEquals(html, "<p>ERROR reaching page </p>");
		method.setAccessible(false);
	}

	public void testBadUrl() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException {
		Class[] argumentClasses = new Class[1];
		argumentClasses[0] = String.class;
		Method method = tabSearch.getClass().getDeclaredMethod("getGuitarTab",
				argumentClasses);
		method.setAccessible(true);

		method.invoke(tabSearch, "bad_url");
		String html = "";
		while ((html = tabSearch.getHtml()).equals("")) {
			;
		}
		assertEquals(html, "<p>ERROR - Bad URL </p>");
		method.setAccessible(false);
	}
}
package com.cs429.amadeus.test;

import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.views.StaffLayout;

import android.app.Instrumentation;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import junit.framework.Assert;
import junit.framework.TestCase;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.cs429.amadeus.R;

public class UiTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mActivity;
    private StaffLayout mStaffLayout;
    private Instrumentation mInstrumentation;
    private Spinner mSpinner;
    private ImageView mImage;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    public UiTest() {
	super(MainActivity.class);

    }

    @UiThreadTest
    protected void setUp() throws Exception {
	super.setUp();
	mInstrumentation = getInstrumentation();
	mActivity = getActivity();
    }

    public void testDrawer() throws InterruptedException {

	selectDrawer(R.string.title_create_music);
	mStaffLayout = (StaffLayout) mActivity
		.findViewById(R.id.fragment_recording_staff_layout);
	assertNotNull(mStaffLayout);

	selectDrawer(R.string.title_play_along);
	mStaffLayout = (StaffLayout) mActivity
		.findViewById(R.id.fragment_play_along_staff_layout);
	Assert.assertNotNull(mStaffLayout);

	selectDrawer(R.string.title_guitar_chords);
	mImage = (ImageView) mActivity
		.findViewById(R.id.fragment_guitar_chords_chord_image);
	Assert.assertNotNull(mImage);

	selectDrawer(R.string.title_guitar_tabs);
	WebView mWeb = ((WebView) getActivity().findViewById(R.id.tab_text));
	assertNotNull(mWeb);

	selectDrawer(R.string.title_home);
	Button button = (Button) mActivity
		.findViewById(R.id.fragment_home_play_along_button);
	assertNotNull(button);

    }

    private void selectDrawer(final int id) {
	mActivity.runOnUiThread(new Runnable() {
	    public void run() {
		mActivity.replaceContentViewOnItemSelected(mActivity
			.getIndexOfItemInDrawer(id));
	    }
	});
	mInstrumentation.waitForIdleSync();
    }

    public void testOnDrawCanvas() {

	mActivity.runOnUiThread(new Runnable() {
	    public void run() {
		((Button) mActivity
			.findViewById(R.id.fragment_home_create_sheet_music_button))
			.performClick();

	    }
	});

	mInstrumentation.waitForIdleSync();

	mStaffLayout = (StaffLayout) mActivity
		.findViewById(R.id.fragment_recording_staff_layout);
	Assert.assertNotNull(mStaffLayout);
    }

    public void testPlayAlong() {

	mActivity.runOnUiThread(new Runnable() {
	    public void run() {
		((Button) mActivity
			.findViewById(R.id.fragment_home_play_along_button))
			.performClick();
	    }
	});
	mInstrumentation.waitForIdleSync();
	mStaffLayout = (StaffLayout) mActivity
		.findViewById(R.id.fragment_play_along_staff_layout);
	Assert.assertNotNull(mStaffLayout);
    }

    /*
     * public void testGuitar() {
     * 
     * mActivity.runOnUiThread(new Runnable() { public void run() { ((Button)
     * mActivity .findViewById(R.id.fragment_home_guitar_chords_button))
     * .performClick(); } }); mInstrumentation.waitForIdleSync(); mImage =
     * (ImageView) mActivity
     * .findViewById(R.id.fragment_guitar_chords_chord_image);
     * Assert.assertNotNull(mImage);
     * 
     * mSpinner = (Spinner) mActivity
     * .findViewById(R.id.fragment_guitar_chords_chord_spinner);
     * mActivity.runOnUiThread(new Runnable() { public void run() {
     * mSpinner.requestFocus(); mSpinner.setSelection(0); } });
     * 
     * // check to see all spinner values are correct. for (int i = 0; i < 15;
     * i++) { this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER); mImage = (ImageView)
     * mActivity .findViewById(R.id.fragment_guitar_chords_chord_image); int pos
     * = mSpinner.getSelectedItemPosition(); int id = (Integer) mImage.getTag();
     * assertEquals(id, R.drawable.a + pos); System.out.println(id);
     * this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
     * 
     * } }
     */

    protected void tearDown() throws Exception {
	super.tearDown();
    }

}
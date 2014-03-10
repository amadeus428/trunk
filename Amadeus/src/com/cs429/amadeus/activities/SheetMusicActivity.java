package com.cs429.amadeus.activities;

import com.cs429.amadeus.R;
import com.cs429.amadeus.StaffView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class SheetMusicActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.staff_example);
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_view);
		frame.addView(new StaffView(this));
	}
}

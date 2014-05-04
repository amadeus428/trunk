package com.cs429.amadeus.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cs429.amadeus.R;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.views.FallingNotesView;

/**
 * The home screen of the app
 */
public class HomeFragment extends Fragment {
	public HomeFragment() {
		// Empty constructor required for fragment subclasses
	}

	public static HomeFragment newInstance() {
		HomeFragment frag = new HomeFragment();

		// add arguments to bundle here

		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		return rootView;
	}

	/**
	 * Creates different buttons to get to different fragments
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("Home");

		final MainActivity parent = (MainActivity) getActivity();
		TextView title = (TextView)getActivity().findViewById(R.id.fragment_home_title_textview);
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "SF_Archery_Black.ttf");
		title.setTypeface(font);
		
		Button sheetMusicBtn = ((Button) getActivity().findViewById(
				R.id.fragment_home_create_sheet_music_button));
		sheetMusicBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				parent.replaceContentViewOnItemSelected(1);
			}
		});
		
		title.setTextSize(sheetMusicBtn.getTextSize() * 1.1f);

		Button playAlongBtn = ((Button) getActivity().findViewById(
				R.id.fragment_home_play_along_button));
		playAlongBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						parent.replaceContentViewOnItemSelected(2);
					}
				});

		Button soundProfileBtn = ((Button) getActivity().findViewById(
				R.id.fragment_home_sound_profile_button));
		soundProfileBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						parent.replaceContentViewOnItemSelected(3);
					}
				});
		
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels / 2;
		sheetMusicBtn.setWidth(width);
		playAlongBtn.setWidth(width);
		soundProfileBtn.setWidth(width);
		
		FrameLayout frame = (FrameLayout)getActivity().findViewById(R.id.fragment_home_main_frame);
		frame.addView(new FallingNotesView(getActivity()), 0);
	}
}

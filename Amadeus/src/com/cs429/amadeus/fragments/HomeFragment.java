package com.cs429.amadeus.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.cs429.amadeus.R;
import com.cs429.amadeus.activities.MainActivity;

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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("Home");

		final MainActivity parent = (MainActivity) getActivity();
		((Button) getActivity().findViewById(R.id.pd_demo_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						parent.replaceContentViewOnItemSelected(1);
					}
				});

		((Button) getActivity().findViewById(R.id.sheet_music_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						parent.replaceContentViewOnItemSelected(2);
					}
				});

		((Button) getActivity().findViewById(R.id.guitar_chords_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						parent.replaceContentViewOnItemSelected(3);
					}
				});
	}
}

package com.cs429.amadeus.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cs429.amadeus.R;
import com.cs429.amadeus.activities.MainActivity;

public class GuitarChordFragment extends Fragment {

	public GuitarChordFragment()
	{
		// Empty constructor required for fragment subclasses
	}

	public static GuitarChordFragment newInstance()
	{
		GuitarChordFragment frag = new GuitarChordFragment();

		// add arguments to bundle here

		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//TODO: Replace the R.layout.fragment_home with your layout
		View rootView = inflater.inflate(R.layout.fragment_guitar_chords, container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("Chords");

		//Do other set up things here, like set up listeners
	}
}

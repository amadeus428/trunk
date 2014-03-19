package com.cs429.amadeus.fragments;

import com.cs429.amadeus.R;
import com.cs429.amadeus.R.layout;
import com.cs429.amadeus.activities.MainActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TabSearchFragment extends Fragment {

	public TabSearchFragment() {
		// Empty constructor required for fragment subclasses
	}

	public static TabSearchFragment newInstance() {
		TabSearchFragment frag = new TabSearchFragment();

		// add arguments to bundle here
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_tab_search, container,
				false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("TabSearch");
	}
}
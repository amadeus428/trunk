package com.cs429.amadeus.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.cs429.amadeus.R;
import com.cs429.amadeus.activities.MainActivity;

public class GuitarChordFragment extends Fragment implements
		OnItemSelectedListener {

	public GuitarChordFragment() {
		// Empty constructor required for fragment subclasses
	}

	public static GuitarChordFragment newInstance() {
		GuitarChordFragment frag = new GuitarChordFragment();

		// add arguments to bundle here

		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO: Replace the R.layout.fragment_home with your layout
		View rootView = inflater.inflate(R.layout.fragment_guitar_chords,
				container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("Chords");

		// Do other set up things here, like set up listeners
		Spinner dropdown = (Spinner) getActivity().findViewById(
				R.id.fragment_guitar_chords_chord_spinner);
		String[] items = new String[] { "A", "A2", "A4", "Am", "C", "D", "D2",
				"D4", "Dm", "D/F#", "E", "E4", "F", "G", "G/B" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item, items);
		dropdown.setAdapter(adapter);
		dropdown.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		ImageView image = (ImageView) getActivity().findViewById(
				R.id.fragment_guitar_chords_chord_image);
		switch (position) {
		case 0:
			image.setImageResource(R.drawable.a);
			break;
		case 1:
			image.setImageResource(R.drawable.a2);
			break;
		case 2:
			image.setImageResource(R.drawable.a4);
			break;
		case 3:
			image.setImageResource(R.drawable.aminor);
			break;
		case 4:
			image.setImageResource(R.drawable.c);
			break;
		case 5:
			image.setImageResource(R.drawable.d);
			break;
		case 6:
			image.setImageResource(R.drawable.d2);
			break;
		case 7:
			image.setImageResource(R.drawable.d4);
			break;
		case 8:
			image.setImageResource(R.drawable.dminor);
			break;
		case 9:
			image.setImageResource(R.drawable.dslashfsharp);
			break;
		case 10:
			image.setImageResource(R.drawable.e);
			break;
		case 11:
			image.setImageResource(R.drawable.e4);
			break;
		case 12:
			image.setImageResource(R.drawable.f);
			break;
		case 13:
			image.setImageResource(R.drawable.g);
			break;
		case 14:
			image.setImageResource(R.drawable.gslashb);
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}

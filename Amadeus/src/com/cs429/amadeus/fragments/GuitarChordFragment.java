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

/**
 * This fragment contains information about playing various beginner chords on
 * guitar
 */
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

	// Called when the view is created
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_guitar_chords,
				container, false);

		return rootView;
	}

	/**
	 * Sets up a drop down menu with options for the various chords
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("Chords");

		Spinner dropdown = (Spinner) getActivity().findViewById(
				R.id.fragment_guitar_chords_chord_spinner);
		String[] items = new String[] { "A", "A2", "A4", "Am", "C", "D", "D2",
				"D4", "Dm", "D/F#", "E", "E4", "F", "G", "G/B" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item, items);
		dropdown.setAdapter(adapter);
		dropdown.setOnItemSelectedListener(this);
	}

	/**
	 * Maps each item in the menu to a picture of the corresponding chord
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		ImageView image = (ImageView) getActivity().findViewById(
				R.id.fragment_guitar_chords_chord_image);
		switch (position) {
		case 0:
			image.setImageResource(R.drawable.a);
			image.setTag(R.drawable.a);
			break;
		case 1:
			image.setImageResource(R.drawable.a2);
			image.setTag(R.drawable.a2);
			break;
		case 2:
			image.setImageResource(R.drawable.a4);
			image.setTag(R.drawable.a4);
			break;
		case 3:
			image.setImageResource(R.drawable.aminor);
			image.setTag(R.drawable.aminor);
			break;
		case 4:
			image.setImageResource(R.drawable.c);
			image.setTag(R.drawable.c);
			break;
		case 5:
			image.setImageResource(R.drawable.d);
			image.setTag(R.drawable.d);
			break;
		case 6:
			image.setImageResource(R.drawable.d2);
			image.setTag(R.drawable.d2);
			break;
		case 7:
			image.setImageResource(R.drawable.d4);
			image.setTag(R.drawable.d4);
			break;
		case 8:
			image.setImageResource(R.drawable.dminor);
			image.setTag(R.drawable.dminor);
			break;
		case 9:
			image.setImageResource(R.drawable.dslashfsharp);
			image.setTag(R.drawable.dslashfsharp);
			break;
		case 10:
			image.setImageResource(R.drawable.e);
			image.setTag(R.drawable.e);
			break;
		case 11:
			image.setImageResource(R.drawable.e4);
			image.setTag(R.drawable.e4);
			break;
		case 12:
			image.setImageResource(R.drawable.f);
			image.setTag(R.drawable.f);
			break;
		case 13:
			image.setImageResource(R.drawable.g);
			image.setTag(R.drawable.g);
			break;
		case 14:
			image.setImageResource(R.drawable.gslashb);
			image.setTag(R.drawable.gslashb);
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}

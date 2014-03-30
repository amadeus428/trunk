package com.cs429.amadeus.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

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
		Spinner dropdown = (Spinner)getActivity().findViewById(R.id.spinner1);
		String[] items = new String[]{"A", "C", "D", "E", "F", "G"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, items);
		dropdown.setAdapter(adapter);
		//dropdown.setOnItemSelectedListener(this.getActivity());
	}
	
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // What ever you want to happen when item 1 selected
            	//ImageView image = (ImageView) getActivity().findViewById(R.id.Achord);
                break;
            case 1:
                // What ever you want to happen when item 2 selected
            	//ImageView image = (ImageView) getActivity().findViewById(R.id.Cchord);
                break;
            case 2:
                // What ever you want to happen when item 3 selected
            	//ImageView image = (ImageView) getActivity().findViewById(R.id.Dchord);
                break;
            case 3:
                // What ever you want to happen when item 3 selected
            	//ImageView image = (ImageView) getActivity().findViewById(R.id.Echord);
                break;
            case 4:
                // What ever you want to happen when item 3 selected
            	//ImageView image = (ImageView) getActivity().findViewById(R.id.Fchord);
                break;
            case 5:
                // What ever you want to happen when item 3 selected
            	//ImageView image = (ImageView) getActivity().findViewById(R.id.Gchord);
                break;
            
        }
    }
}

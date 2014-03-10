package com.cs429.amadeus.fragments;

import com.cs429.amadeus.R;
import com.cs429.amadeus.R.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

	public HomeFragment() {
        // Empty constructor required for fragment subclasses
    }
    
    public static HomeFragment newInstance() {
    	HomeFragment frag = new HomeFragment();
    	
    	//add arguments to bundle here
    	
    	return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	getActivity().setTitle("Home");
    	//call functions to set up listeners/things in here
    }
}

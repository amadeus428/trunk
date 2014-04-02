package com.cs429.amadeus.fragments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.puredata.android.service.PdService;

import com.cs429.amadeus.R;
import com.cs429.amadeus.R.layout;
import com.cs429.amadeus.TabView;
import com.cs429.amadeus.activities.MainActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

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

	View rootView = inflater.inflate(R.layout.fragment_tab, container,
		false);

	return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	getActivity().setTitle("TabSearch");

	((Button) getActivity().findViewById(R.id.tab_button))
		.setOnClickListener(new OnClickListener() {
		    public void onClick(View arg) {
			onButtonClick();
		    }
		});
    }

    /**
     * Function called by the listener attached to the button
     */
    private void onButtonClick() {
	String artist = ((EditText) getActivity().findViewById(
		R.id.tab_artist_box)).getText().toString();
	String song = ((EditText) getActivity().findViewById(R.id.tab_song_box))
		.getText().toString();
	// make sure the user has entered information into the boxes
	if (song == null || artist == null || song.length() < 1
		|| artist.length() < 1) {
	    return;
	}
	String url = buildUrl(artist, song);
	getHtml(url);
    }

    /**
     * Given the artist and the song that the user wants to pull up the tab for
     * this method will build the url to query GuritarTabs.com
     * 
     * @param artist
     *            - name of the artist that the user wants to query
     * @param song
     *            - name of the song that the user wants to query
     * @return
     */
    private String buildUrl(String artist, String song) {

	String firstLetterArtist = artist.substring(0, 1);
	// change artist and song to be valid query parts of the url
	artist = artist.trim();
	song = song.trim();
	artist = artist.toLowerCase();
	song = song.toLowerCase();
	artist = artist.replace(" ", "_");
	song = song.replace(" ", "_");

	return "http://tabs.ultimate-guitar.com/" + firstLetterArtist + "/"
		+ artist + "/" + song + "_tab.htm";
    }

    /**
     * Gets the html data from ultimate-guitar.com with the given url
     * 
     * @param url
     *            - formed ultimate-guitar.com url
     * @return - html data of the webpage or "" on error
     */
    private void getHtml(String url) {

	FetchInternetData fetcher = new FetchInternetData();
	fetcher.execute(url);

    }

    /**
     * Class needed to run in background to do the internet work since it can't
     * be done on the main thread
     * 
     */
    private class FetchInternetData extends AsyncTask<String, Integer, String> {
	protected String doInBackground(String... urls) {
	    String html = "";
	    try {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(urls[0]);
		HttpResponse response = client.execute(request);

		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(in));
		StringBuilder str = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
		    str.append(line);
		}
		in.close();
		html = str.toString();
	    } catch (IOException io) {
		Log.w("TabView", "IO Exception");
	    }
	    Log.w("Tabview", "html = " + html);
	    return html;
	}

	protected void onPostExecute(String result) {
	    setText(result);
	}
    }

    private void setText(String html) {
	((WebView) getActivity().findViewById(R.id.tab_text))
		.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    /**
     * Parses the html in order to keep only the relevant parts of the html
     * 
     * @param html
     * @return
     */
    private String parseHtml(String html) {
	return html;
    }
}
package com.cs429.amadeus.fragments;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cs429.amadeus.R;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

/**
 * Handles searching for tabs and lyrics for a song based on user input
 */
public class TabSearchFragment extends Fragment {

	private String html = "";

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
	 * Called when the GO button is clicked. Function creates the url from the
	 * artist and song entered by the user and will then get the guitar tab
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
		getGuitarTab(url);
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

		// parse information from text boxes
		artist = artist.trim();
		song = song.trim();
		artist = artist.toLowerCase();
		song = song.toLowerCase();

		String firstLetterArtist = artist.substring(0, 1);
		// check if the first letter is a number and not a letter
		try {
			Integer.parseInt(firstLetterArtist);
			firstLetterArtist = "0-9";
		} catch (NumberFormatException e) {

		}

		// clean up the artist and song name to create the url
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
	private void getGuitarTab(String url) {
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
			String html = "<p> hello world </p>";
			try {
				// connect to the server and get the document
				URL url = new URL(urls[0]);
				Document doc = Jsoup.connect(url.toString()).get();

				// parse the document in order to only get the tab part
				Elements tab_elements = doc.getElementsByClass("tb_ct");
				Element tab_element = tab_elements.get(0);
				Elements pres = tab_element.getElementsByTag("pre");
				html = pres.get(2).toString();

			} catch (MalformedURLException e) {
				html = "<p>ERROR - Bad URL </p>";
			} catch (HttpStatusException e) {
				html = "<p>ERROR reaching page </p>";
			} catch (Exception e) {
				e.printStackTrace();
				html = "<p> An error occured </p>";
			}

			return html;
		}

		protected void onPostExecute(String result) {
			setWebViewHtml(result);
		}
	}

	/**
	 * Returns the html currently in the webview
	 * 
	 * @return
	 */
	public String getHtml() {
		return html;
	}

	/**
	 * Sets the html to be displayed in the webview to the html given in
	 * 
	 * @param html
	 */
	private void setWebViewHtml(String html) {
		this.html = html;
		((WebView) getActivity().findViewById(R.id.tab_text))
				.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	}
}
package com.cs429.amadeus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class TabView {
	
	/**
	 * Given the artist and the song that the user wants to pull up the tab for
	 * this method will build the url to query GuritarTabs.com 
	 * @param artist - name of the artist that the user wants to query 
	 * @param song - name of the song that the user wants to query
	 * @param extension - extension to be added the end of the url is either tab or crd
	 * @return
	 */
	private String urlBuilder(String artist, String song, String extension){
		
		String firstLetterArtist = artist.substring(0, 1);
		//change artist and song to be valid query parts of the url
		artist = artist.toLowerCase();
		song = song.toLowerCase();
		artist.replace(" ", "_");
		song.replace(" ", "_");
		song = song + "_" + extension;
		
		return "http://tabs.ultimate-guitar.com/" 
				+ firstLetterArtist + "/" + artist + "/" + song + ".htm";
	}

	/**
	 * Gets the html data from ultimate-guitar.com with the given url
	 * @param url - formed ultimate-guitar.com url
	 * @return - html data of the webpage or "" on error
	 */
	private String getHtml(String url)  {

		String html = "";
		try{
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);

			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null)
			{
			    str.append(line);
			}
			in.close();
			html = str.toString();
		}
		catch (IOException io){
			Log.w("TabView", "IO Exception");
		}
		return html;
	}
	
	/**
	 * Parses the html in order to keep only the relevant parts of the html
	 * @param html
	 * @return
	 */
	private String parseHtml(String html){
		return html;
	}
}
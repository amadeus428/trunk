package com.cs429.amadeus.helpers;

import java.util.HashMap;
import java.util.Map.Entry;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class SoundProfile {
	private HashMap<Range, String> rangeToFileMap = new HashMap<Range, String>();

	public SoundProfile() {
	}

	public void addMapping(float low, float high, String audioFilePath) {
		try {
			if (low < 0 || high < 0 || low > high || audioFilePath == null) {
				return;
			}

			Range range = new Range(low, high);
			rangeToFileMap.put(range, audioFilePath);
		} catch (Exception e) {
			Log.e("TEST", e.toString());
		}
	}

	public void play(float freq) {
		for (Entry<Range, String> entry : rangeToFileMap.entrySet()) {
			Range range = entry.getKey();
			if (freq >= range.low && freq <= range.high) {
				try {
					String path = entry.getValue();
					MediaPlayer sound = new MediaPlayer();
					sound.setDataSource(path);
					sound.prepare();
					sound.start();
					return;
				} catch (Exception e) {
					Log.e("TEST", e.toString());
				}
			}
		}
	}

	public HashMap<Range, String> getMap() {
		return rangeToFileMap;
	}

	public class Range {
		public float low;
		public float high;

		public Range(float low, float high) {
			this.low = low;
			this.high = high;
		}

		@Override
		public boolean equals(Object o) {
			Range other = (Range) o;
			return other.low == low && other.high == high;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}
}

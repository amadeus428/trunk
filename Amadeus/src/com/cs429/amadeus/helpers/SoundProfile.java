package com.cs429.amadeus.helpers;

import java.util.HashMap;
import java.util.Map.Entry;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

public class SoundProfile {
	private SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	private HashMap<Range, String> rangeToFilePathMap = new HashMap<Range, String>();
	private HashMap<String, Integer> filePathToSoundIdMap = new HashMap<String, Integer>();

	public SoundProfile() {
	}

	public void addMapping(float low, float high, String audioFilePath) throws Exception {
		if (low < 0 || high < 0) {
			throw new Exception("A mapping contains negative values.");
		}
		
		if(audioFilePath == null || audioFilePath.isEmpty()) {
			throw new Exception("A mapping's file path has not been set.");
		}
		
		if(low > high) {
			throw new Exception("A mapping's low > high.");
		}
		
		if(overlapExists(low, high)) {
			throw new Exception("A range overlap exists.");
		}

		Range range = new Range(low, high);
		rangeToFilePathMap.put(range, audioFilePath);
		
		int id = soundPool.load(audioFilePath, 0);
		filePathToSoundIdMap.put(audioFilePath, id);	
	}

	public void play(float freq) {
		for (Entry<Range, String> entry : rangeToFilePathMap.entrySet()) {
			Range range = entry.getKey();
			if (freq >= range.low && freq <= range.high) {
				try {
					String path = entry.getValue();
					int id = filePathToSoundIdMap.get(path);
					soundPool.play(id, 1, 1, 1, 0, 1);
					return;
				} catch (Exception e) {
					Log.e("TEST", e.toString());
				}
			}
		}
	}

	public HashMap<Range, String> getMap() {
		return rangeToFilePathMap;
	}
	
	private boolean overlapExists(float low, float high)
	{
		for (Entry<Range, String> entry : rangeToFilePathMap.entrySet()) {
			Range range = entry.getKey();
			
			boolean case1 = low <= range.low && high >= range.low;
			boolean case2 = low <= range.high && high >= range.high;
			boolean case3 = low >= range.low && high <= range.high;
			if(case1 || case2 || case3) {
				return true;
			}
		}
		
		return false;
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

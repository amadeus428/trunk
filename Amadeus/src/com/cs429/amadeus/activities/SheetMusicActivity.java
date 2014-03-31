package com.cs429.amadeus.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.R;
import com.cs429.amadeus.views.StaffLayout;

public class SheetMusicActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sheet_music);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		final StaffLayout staffLayout = (StaffLayout) findViewById(R.id.activity_sheet_music_staff_layout);

		final Button openButton = (Button) findViewById(R.id.activity_sheet_music_open_sheet_button);
		openButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		final Spinner noteTypeSpinner = (Spinner) findViewById(R.id.activity_sheet_music_note_spinner);
		noteTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
					int pos, long id) {
				switch (pos) {
				case 0:
					staffLayout.setAddNoteType(Note.QUARTER_NOTE);
					break;
				case 1:
					staffLayout.setAddNoteType(Note.WHOLE_NOTE);
					break;
				case 2:
					staffLayout.setAddNoteType(Note.HALF_NOTE);
					break;
				case 3:
					staffLayout.setAddNoteType(Note.EIGHTH_NOTE);
					break;
				case 4:
					staffLayout.setAddNoteType(Note.SIXTEENTH_NOTE);
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
			}
		});
	}
}

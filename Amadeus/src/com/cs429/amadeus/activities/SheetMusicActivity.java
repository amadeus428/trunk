
package com.cs429.amadeus.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

import com.cs429.amadeus.R;
import com.cs429.amadeus.views.StaffLayout;

public class SheetMusicActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sheet_music);
		
		final StaffLayout staffLayout = (StaffLayout)findViewById(R.id.activity_sheet_music_staff_layout);
		
		final Button openButton = (Button)findViewById(R.id.activity_sheet_music_open_sheet_button); 
		openButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				
			}			
		});
		
		final Spinner noteTypeSpinner = (Spinner)findViewById(R.id.activity_sheet_music_note_spinner);
		noteTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, int pos, long id)
			{
				switch(pos)
				{
					case 0:
						staffLayout.setCurrAddNoteType(StaffLayout.QUARTER_NOTE_DOWN);
						break;
					case 1:
						staffLayout.setCurrAddNoteType(StaffLayout.WHOLE_NOTE);
						break;
					case 2:
						staffLayout.setCurrAddNoteType(StaffLayout.HALF_NOTE_DOWN);
						break;
					case 3:
						staffLayout.setCurrAddNoteType(StaffLayout.EIGHTH_NOTE_DOWN);
						break;
					case 4:
						staffLayout.setCurrAddNoteType(StaffLayout.SIXTEENTH_NOTE_DOWN);
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter)
			{
			}		
		});
	}
}

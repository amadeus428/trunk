
package com.cs429.amadeus.activities;

import com.cs429.amadeus.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SheetMusicActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.staff_example);
		
		Button openButton = (Button)findViewById(R.id.open_sheet_button); 
		openButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}			
		});
	}
}

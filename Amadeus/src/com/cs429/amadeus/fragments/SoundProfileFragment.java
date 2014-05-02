package com.cs429.amadeus.fragments;

import java.io.File;
import java.util.Map.Entry;

import com.cs429.amadeus.R;
import com.cs429.amadeus.helpers.OpenSaveHelper;
import com.cs429.amadeus.helpers.SoundProfile;
import com.cs429.amadeus.helpers.SoundProfile.Range;
import com.daidalos.afiledialog.FileChooserDialog;
import com.daidalos.afiledialog.FileChooserDialog.OnFileSelectedListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SoundProfileFragment extends Fragment {
	private LinearLayout list;
	private AlertDialog.Builder openDialog;
	private AlertDialog.Builder saveDialog;
	private FileChooserDialog browseDialog;

	public SoundProfileFragment() {
	}

	public static SoundProfileFragment newInstance() {
		return new SoundProfileFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sound_profile, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("Sound Profile");

		list = (LinearLayout) getActivity().findViewById(
				R.id.fragment_sound_profile_mapping_list);

		createButtonListeners();

		((Button) getActivity().findViewById(
				R.id.fragment_sound_profile_add_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						final LinearLayout item = (LinearLayout) getActivity()
								.getLayoutInflater()
								.inflate(
										R.layout.fragment_sound_profile_add_item,
										null);
						createButtonListeners(item);

						list.addView(item);
					}
				});
	}

	private void createButtonListeners() {
		((Button) getActivity().findViewById(
				R.id.fragment_sound_profile_open_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						createOpenDialog();
						openDialog.show();
					}
				});

		((Button) getActivity().findViewById(
				R.id.fragment_sound_profile_save_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(createSaveDialog()) {
							saveDialog.show();
						}
					}
				});
	}

	private void createButtonListeners(final LinearLayout item) {
		((Button) item
				.findViewById(R.id.fragment_sound_profile_add_item_remove_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						list.removeView(item);
					}
				});

		((Button) item
				.findViewById(R.id.fragment_sound_profile_add_item_browse_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						createBrowseDialog(item);
						browseDialog.show();
					}
				});
	}

	private void createOpenDialog() {
		File profilesRoot = getActivity().getDir(
				OpenSaveHelper.SOUND_PROFILES_DIR, Context.MODE_PRIVATE);
		final String[] fileNames = profilesRoot.list();

		openDialog = new AlertDialog.Builder(getActivity());
		openDialog
				.setTitle("Open sound profile")
				.setItems(fileNames, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String filename = fileNames[which];
						openProfile(filename);
						dialog.dismiss();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});
	}

	private boolean createSaveDialog() {
		String error = saveProfile("test", true);
		if(error != null) {
			Log.e("TEST", "FUCKKKKK");
			Toast.makeText(SoundProfileFragment.this.getActivity(), error, Toast.LENGTH_LONG).show();
			return false;
		}
		
		final EditText input = new EditText(getActivity());
		saveDialog = new AlertDialog.Builder(getActivity());
		saveDialog
				.setTitle("Save profile")
				.setMessage("Enter file name")
				.setView(input)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String path = input.getText().toString();
						saveProfile(path, false);
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
		
		return true;
	}

	private void createBrowseDialog(final LinearLayout item) {
		browseDialog = new FileChooserDialog(getActivity());
		browseDialog.addListener(new OnFileSelectedListener() {
			@Override
			public void onFileSelected(Dialog source, File file) {
				TextView pathText = (TextView) item
						.findViewById(R.id.fragment_sound_profile_add_item_file_path);
				pathText.setText(file.getAbsolutePath());
				Log.e("TEST", file.getAbsolutePath());
				source.hide();
			}

			@Override
			public void onFileSelected(Dialog source, File folder, String name) {
			}
		});
	}

	private void openProfile(String filePath) {
		list.removeAllViews();

		SoundProfile profile = OpenSaveHelper.openSoundProfile(getActivity(),
				filePath);
		for (Entry<Range, String> entry : profile.getMap().entrySet()) {
			Range range = entry.getKey();
			String low = "" + range.low;
			String high = "" + range.high;
			String path = entry.getValue();

			LinearLayout item = (LinearLayout) getActivity()
					.getLayoutInflater().inflate(
							R.layout.fragment_sound_profile_add_item, null);
			EditText lowText = (EditText) item
					.findViewById(R.id.fragment_sound_profile_add_item_low);
			EditText highText = (EditText) item
					.findViewById(R.id.fragment_sound_profile_add_item_high);
			TextView pathText = (TextView) item
					.findViewById(R.id.fragment_sound_profile_add_item_file_path);

			lowText.setText(low);
			highText.setText(high);
			pathText.setText(path);

			createButtonListeners(item);

			list.addView(item);
		}
	}

	private String saveProfile(String filePath, boolean isTest) {
		SoundProfile profile = new SoundProfile();
		for (int i = 0; i < list.getChildCount(); i++) {
			LinearLayout item = (LinearLayout) list.getChildAt(i);
			EditText lowText = (EditText) item
					.findViewById(R.id.fragment_sound_profile_add_item_low);
			EditText highText = (EditText) item
					.findViewById(R.id.fragment_sound_profile_add_item_high);
			TextView filePathText = (TextView) item
					.findViewById(R.id.fragment_sound_profile_add_item_file_path);
			
			try 
			{
				final float low = Float.parseFloat(lowText.getText().toString());
				final float high = Float.parseFloat(highText.getText().toString());
				final String path = filePathText.getText().toString();
				profile.addMapping(low, high, path);
			}
			catch(Exception e)
			{
				return e.getMessage();
			}
		}

		if(!isTest)
		{
			OpenSaveHelper.saveSoundProfile(getActivity(), filePath, profile);
		}
		
		return null;
	}
}
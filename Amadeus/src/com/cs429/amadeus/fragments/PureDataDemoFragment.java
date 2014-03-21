
package com.cs429.amadeus.fragments;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;

import com.cs429.amadeus.Note;
import com.cs429.amadeus.R;
import com.cs429.amadeus.R.drawable;
import com.cs429.amadeus.R.id;
import com.cs429.amadeus.R.layout;
import com.cs429.amadeus.R.raw;
import com.cs429.amadeus.StaffLayout;
import com.cs429.amadeus.StaffView;
import com.cs429.amadeus.activities.MainActivity;
import com.cs429.amadeus.helpers.NoteCalculator;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PureDataDemoFragment extends Fragment
{
	private long lastNoteTime = 0;
	private float noteCooldown = 1000;
	private StaffLayout staffLayout;
	private PdUiDispatcher dispatcher;
	private PdService pdService = null;
	
	private final ServiceConnection pdConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			pdService = ((PdService.PdBinder)service).getService();
			try
			{
				initPd();
				loadPatch();
			}
			catch (IOException e)
			{
				Log.e("TAG", e.toString());
				getActivity().finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			// this method will never be called
		}
	};

	public PureDataDemoFragment()
	{
		// Empty constructor required for fragment subclasses
	}

	public static PureDataDemoFragment newInstance()
	{
		PureDataDemoFragment frag = new PureDataDemoFragment();

		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_record, container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("Record");

		// call functions to set up listeners/things in heres

		staffLayout = (StaffLayout)getActivity().findViewById(R.id.demo_staffLayout);

		initSystemServices();
		getActivity().bindService(new Intent(getActivity(), PdService.class), pdConnection,
				getActivity().BIND_AUTO_CREATE);

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		getActivity().unbindService(pdConnection);
	}

	private void initPd() throws IOException
	{
		// Configure the audio glue
		AudioParameters.init(getActivity());
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 1, 2, 10.0f);
		startPDService();

		// Create and install the dispatcher
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter()
		{
			@Override
			public void receiveFloat(String source, final float x)
			{
				long currTime = Calendar.getInstance().getTimeInMillis();
				if(currTime - lastNoteTime > noteCooldown)
				{
					lastNoteTime = currTime;
					updateStaffView(x);
				}
			}
		});
	}

	private void updateStaffView(float x)
	{
		Note note = NoteCalculator.getNoteFromMIDI((double)x);
		staffLayout.addNote(note);
		Toast.makeText(getActivity(), note.toString(), Toast.LENGTH_SHORT).show();
	}

	private void startPDService()
	{
		if(!pdService.isRunning())
		{
			Intent intent = new Intent(((MainActivity)getActivity()), MainActivity.class);
			pdService.startAudio(intent, R.drawable.icon, "Amadeus", "Return to Amadeus");
		}
	}

	private void loadPatch() throws IOException
	{
		File dir = getActivity().getFilesDir();
		IoUtils.extractZipResource(getResources().openRawResource(R.raw.tuner), dir, true);
		File patchFile = new File(dir, "tuner.pd");
		PdBase.openPatch(patchFile.getAbsolutePath());
	}

	private void initSystemServices()
	{
		TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener()
		{
			@Override
			public void onCallStateChanged(int state, String incomingNumber)
			{
				if(pdService == null)
					return;
				if(state == TelephonyManager.CALL_STATE_IDLE)
				{
					startPDService();
				}
				else
				{
					pdService.stopAudio();
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

}

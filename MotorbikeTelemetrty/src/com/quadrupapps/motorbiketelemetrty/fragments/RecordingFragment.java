package com.quadrupapps.motorbiketelemetrty.fragments;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quadrupapps.motorbiketelemetrty.MBTMainActivity;
import com.quadrupapps.motorbiketelemetrty.R;
import com.quadrupapps.motorbiketelemetrty.sensors.SensorReader;
import com.quadrupapps.motorbiketelemetrty.sensors.SensorsValue;
import com.quadrupapps.motorbiketelemetrty.views.AngleIndicator;
import com.quadrupapps.motorbiketelemetrty.views.RecorderPreview;

public class RecordingFragment extends Fragment implements OnClickListener {
	
	//Create objects of MediaRecorder and Preview class
	private MediaRecorder recorder;
	private RecorderPreview mRecorderPreview;

	// azimut, pitch, roll;
	private float[] orientation;
	
	// x y z
	private float[] acceleration;
	
	public TextView mTextView             = null;
	public TextView recordingTextview     = null;
	public AngleIndicator mAngleIndicator = null;
	OrientationHandler mHandler           = null;
	SensorReader mSensorReader            = null;
	
	boolean isRecordingOnDb = false;
	
	View rootView = null;
	
	RelativeLayout mainLayout = null;
	
	public RecordingFragment() {}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_mbtmain,
				container, false);
		
		mAngleIndicator = (AngleIndicator) rootView.findViewById(R.id.angle);
		
		// setto i listener sui bolttoni del layout del fragmnentr
		rootView.findViewById(R.id.startButton).setOnClickListener(this);
		rootView.findViewById(R.id.stopButton).setOnClickListener(this);
		
		recordingTextview = (TextView) rootView.findViewById(R.id.recordingView);
		
		mHandler = new OrientationHandler();
		mSensorReader = new SensorReader(getActivity(), mHandler);
		mSensorReader.sensorReaderOnResume();
		
		mainLayout       = (RelativeLayout)  rootView.findViewById (R.id.mainLayout);
		mRecorderPreview = (RecorderPreview) rootView.findViewById (R.id.recorderPreview);
					
		Log.i("","mainlayout is null " + (mainLayout == null));
		Log.i("","preview is null " + (mRecorderPreview == null));
		
		return rootView;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSensorReader.sensorReaderOnPause();
	}
	
	
	/**
	 *  Send the obtained bytes to the UI activity
	 * mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
	 */	
	public class OrientationHandler extends Handler {
		 @Override
		 public void handleMessage(Message msg) {	
			 Bundle bundle = msg.getData();
		     if(msg.what == SensorReader.NEW_ORIENTATION) {	
		    	 orientation = (float[]) msg.obj;
		    	
		    	 // Gestisco arrivo inclinazioni
		    	 mAngleIndicator.setMotoAngle(- orientation[2]);
		    	 
		    	 if(isRecordingOnDb){
		    		 MBTMainActivity.mDbHandler.addSensorValue(new SensorsValue(
		    				 0, // id
		    				 System.currentTimeMillis(), //time
		    				 orientation[0], // pitch
		    				 orientation[1], // roll 
		    				 orientation[2], // heading
		    				 orientation[3], //accx
		    				 orientation[4], // accy
		    				 orientation[5]  // accz
		    				 ));
		    	 }
		     }
		 }
	}
	
	
	private void showTrackNameAlert(){
		 // This example shows how to add a custom layout to an AlertDialog
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
        new AlertDialog.Builder(getActivity())
            .setIconAttribute(android.R.attr.alertDialogIcon)
            .setTitle("Track Name")
            .setView(textEntryView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	
                	String title  = ((EditText)textEntryView.findViewById(R.id.name_edit)).getText().toString();
                	String notes = ((EditText)textEntryView.findViewById(R.id.note_edit)).getText().toString();
                	
                	startTrackRecording(title, notes);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
            
                }
            })
            .show();			
	}
	
	@Override
	public void onClick( View v ){
		switch(v.getId()){
		case R.id.startButton:
			showTrackNameAlert();
			break;
		case R.id.stopButton:
			isRecordingOnDb = false;
			recordingTextview.setText("");
			mRecorderPreview.stopRecording();
			break;
		}
	}
	
	
	private void startTrackRecording(String title, String notes){
		isRecordingOnDb = true;
		recordingTextview.setText("RECORDING");
		MBTMainActivity.mDbHandler.addNewTrack(title, notes);
		mRecorderPreview.startRecording(title);
	}

	private void logAllValues(){
		ArrayList<SensorsValue> values = MBTMainActivity.mDbHandler.getAllValues();
		
		for(SensorsValue sv : values){
			Log.i("", sv.getId() + " " + sv.getId() + " " + sv.getTime()+ " " + sv.getTilt()+ " " + sv.getRoll()+ " " + sv.getHeading());
		}
	}
	
	private void logAllTrackNames(){
		ArrayList<String> values = MBTMainActivity.mDbHandler.getTracksNames();
		
		for(String sv : values){
			Log.i("TRACK: ", sv);
		}
	}
	
	
	
}
package com.quadrupapps.motorbiketelemetrty;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quadrupapps.motorbiketelemetrty.db.DBHandler;
import com.quadrupapps.motorbiketelemetrty.sensors.SensorReader;
import com.quadrupapps.motorbiketelemetrty.sensors.SensorsValue;
import com.quadrupapps.motorbiketelemetrty.views.AngleIndicator;
import com.quadrupapps.motorbiketelemetrty.views.RecorderPreview;

public class MBTMainActivity extends Activity {

	public static DBHandler mDbHandler = null;
	public Fragment recordingFregment = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mbtmain);

		mDbHandler = new DBHandler(this);
		
		recordingFregment = new PlaceholderFragment();
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, recordingFregment)
					.commit();
		}
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mbtmain, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener {
		
		//Create objects of MediaRecorder and Preview class
		private MediaRecorder recorder;
		private RecorderPreview preview;
	
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
		
		RelativeLayout mainLayout = null;
				
		public PlaceholderFragment() {
		}

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
			
			mainLayout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);
			
//		    recorder = new MediaRecorder();
//		    preview = new RecorderPreview(getActivity(), recorder);
		    
		    //mainLayout.addView(preview);
			
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
				//logAllValues();
				logAllTrackNames();
				break;
			}
		}
		
		
		private void startTrackRecording(String title, String notes){
			isRecordingOnDb = true;
			recordingTextview.setText("RECORDING");
			MBTMainActivity.mDbHandler.addNewTrack(title, notes);
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
		


	

}

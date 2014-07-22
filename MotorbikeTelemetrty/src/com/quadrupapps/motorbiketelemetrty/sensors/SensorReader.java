package com.quadrupapps.motorbiketelemetrty.sensors;

import java.util.Calendar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

public class SensorReader implements SensorEventListener{

	public static final int NEW_ORIENTATION = 0x01;	
	
	private SensorManager mSensorManager;
	private Sensor accelerometer;
	private Sensor magnetometer;
	
	private float azimut, pitch, roll;
	
	private Calendar calendar = null; 	
	private Context ctx       = null;
	
	float[] mGravity     = {0.0f, 0.0f, 0.0f};
	float[] mPrevGravity = {0.0f, 0.0f, 0.0f};
	float[] mGeomagnetic = {0.0f, 0.0f, 0.0f};
	
	private Handler mHandler = null;
	
	public SensorReader(Context ctx, Handler handler) {
		this.ctx = ctx;
		this.mHandler = handler;
		
		// Register the sensor listeners
	    mSensorManager = (SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);
	    accelerometer  = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    magnetometer   = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	    
	    calendar = Calendar.getInstance();
	}

	public void sensorReaderOnResume(){
	    mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	    mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void sensorReaderOnPause(){
       mSensorManager.unregisterListener(this);
	}
	
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {	}
	
	
	float orientation[];
	float dataToSend[];
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
	    	mGravity = event.values;
	    }
	    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
	    	mGeomagnetic = event.values;
	    }	
	    if (mGravity != null && mGeomagnetic != null) {
	    	float R[] = new float[9];
	    	float I[] = new float[9];
	    	boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
	    	
	    	if (success) {
		        orientation = new float[3];
		        SensorManager.getOrientation(R, orientation);
		        azimut = orientation[0];
		        pitch  = orientation[1];
		        roll   = orientation[2];
		        
		        dataToSend = new float[6];
		        dataToSend[0] =  azimut * (float)(180/Math.PI);
		        dataToSend[1] =  roll * (float)(180/Math.PI);
		        dataToSend[2] =  pitch * (float)(180/Math.PI);
		        dataToSend[3] =  mGravity[0];
		        dataToSend[4] =  mGravity[1];
		        dataToSend[5] =  mGravity[2];
		        
		        mHandler.obtainMessage(SensorReader.NEW_ORIENTATION, dataToSend.length, -1, dataToSend).sendToTarget();
	    	}	
	    }
		
	}

	
	private long getTimestamp(){
		return System.currentTimeMillis();
	}
	
	
	
}

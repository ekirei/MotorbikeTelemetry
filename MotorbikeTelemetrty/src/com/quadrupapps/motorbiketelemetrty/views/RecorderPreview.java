package com.quadrupapps.motorbiketelemetrty.views;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class RecorderPreview extends SurfaceView implements SurfaceHolder.Callback {
      //Create objects for MediaRecorder and SurfaceHolder.
      MediaRecorder recorder;
      SurfaceHolder holder;
      Camera mCamera;

      Context mContext = null;
      
      
      
      //Create constructor of Preview Class. In this, get an object of
      //surfaceHolder class by calling getHolder() method. After that add
      //callback to the surfaceHolder. The callback will inform when surface is
      //created/changed/destroyed. Also set surface not to have its own buffers.

      public RecorderPreview(Context context, AttributeSet attrs) {
		 super(context, attrs);
		 mContext = context;
	     recorder = new MediaRecorder();
		 holder = getHolder();
		 holder.addCallback(this);
		 holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       }

      
      
      
      
      // Implement the methods of SurfaceHolder.Callback interface

      // SurfaceCreated : This method gets called when surface is created.
      // In this, initialize all parameters of MediaRecorder object as explained
      // above.

      public void surfaceCreated(SurfaceHolder holder){    	  
		  try{
	           	recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);	
	           	recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			   	recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);           	
	           	recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			   	recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		    	recorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/" + "test.mpeg");
		    	recorder.setPreviewDisplay(holder.getSurface());
		    	recorder.prepare();
		    	Log.i("surfaceCreated", "recorder prepared");
		    } catch (Exception e) {
		    	Log.e("startRecording", e.toString());
	      }
	  }

      
      
      
      public void surfaceDestroyed(SurfaceHolder holder) {
          if(recorder!=null){
        	  recorder.release();
        	  recorder = null;
          }
      }

      
      
      
      //surfaceChanged : This method is called after the surface is created.
      public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	  
      }
      
      
      
      
      public void startRecording(String filename){
    	  try {
    		  String filePath = mContext.getFilesDir() + "/" + filename;
	    	  recorder.reset();
	    	  recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);	
	     	  recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		      recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);           	
	     	  recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		      recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	    	  recorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/" + filePath + ".mpeg");
	    	  recorder.setPreviewDisplay(holder.getSurface());
	    	  recorder.prepare();
			  recorder.start();
			  
		   } catch (IllegalStateException | IOException e) {
				e.printStackTrace();
		   }
       }
      
      
      
      
      public void stopRecording(){
    	  try {
			recorder.stop();
		} catch (Exception e) {
			Log.e("","stoprecorder fail diomerda");
		}
      }
      
      
      
      
}
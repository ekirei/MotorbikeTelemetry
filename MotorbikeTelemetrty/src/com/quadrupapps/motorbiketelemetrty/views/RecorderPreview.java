package com.quadrupapps.motorbiketelemetrty.views;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class RecorderPreview extends SurfaceView implements SurfaceHolder.Callback {
      //Create objects for MediaRecorder and SurfaceHolder.
      MediaRecorder recorder;
      SurfaceHolder holder;
      Camera mCamera;

      //Create constructor of Preview Class. In this, get an object of
      //surfaceHolder class by calling getHolder() method. After that add
      //callback to the surfaceHolder. The callback will inform when surface is
      //created/changed/destroyed. Also set surface not to have its own buffers.

      public RecorderPreview(Context context,  AttributeSet attrs) {
		 super(context);
	     recorder = new MediaRecorder();
		 holder = getHolder();
		 holder.addCallback(this);
		 holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		 mCamera = Camera.open();
       }

      // Implement the methods of SurfaceHolder.Callback interface

      // SurfaceCreated : This method gets called when surface is created.
      // In this, initialize all parameters of MediaRecorder object as explained
      // above.

      public void surfaceCreated(SurfaceHolder holder){
    	  try {
              mCamera.setPreviewDisplay(this.getHolder());
          } catch (Exception e) {
              e.printStackTrace();
          }
    	  
		  try{
	           	recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
			   	recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			   	recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		    	recorder.setOutputFile("/sdcard/recordvideooutput.3gpp");
		    	recorder.setPreviewDisplay(holder.getSurface());
		    	recorder.prepare();
		    } catch (Exception e) {
		    	String message = e.getMessage();
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
		  Camera.Parameters params = mCamera.getParameters();
		  List<Camera.Size> sizes = params.getSupportedPreviewSizes();
		  Camera.Size selected = sizes.get(0);
		  params.setPreviewSize(selected.width,selected.height);
		  mCamera.setParameters(params);
		
		  mCamera.startPreview();
      }
      
}
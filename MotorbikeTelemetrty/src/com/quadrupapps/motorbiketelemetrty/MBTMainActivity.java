package com.quadrupapps.motorbiketelemetrty;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.quadrupapps.motorbiketelemetrty.db.DBHandler;
import com.quadrupapps.motorbiketelemetrty.fragments.RecordingFragment;

public class MBTMainActivity extends Activity {

	public static DBHandler mDbHandler = null;
	public Fragment recordingFregment = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mbtmain);

		mDbHandler = new DBHandler(this);
		
		recordingFregment = new RecordingFragment();
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, recordingFregment)
					.commit();
		}
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mbtmain, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

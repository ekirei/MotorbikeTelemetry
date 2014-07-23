package com.quadrupapps.motorbiketelemetrty.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.quadrupapps.motorbiketelemetrty.sensors.SensorsValue;

public class DBHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION  = 1;

	// Database Name
	private static final String DATABASE_NAME  = "motorbikeTelemetry";

	// Contacts table name
	private static final String TABLE_SENSOR_VALUES = "sensorsTrack";
	private static final String TABLE_TRACKS = "tracks";
	private static final String TABLE_TRACKS_ENDS = "trackends";

	// Contacts Table Columns names
	private static final String KEY_ID       = "id";
	private static final String KEY_TIME     = "time";
	private static final String KEY_TILT     = "tilt";
	private static final String KEY_ROLL     = "roll";
	private static final String KEY_HEADING  = "heading";
	private static final String KEY_ACC_X    = "accx";
	private static final String KEY_ACC_Y    = "accy";
	private static final String KEY_ACC_Z    = "accz";
	
	
	// Contacts Table Columns names
	private static final String KEY_TRACKS_ID        = "id";
	private static final String KEY_TRACKS_NAME      = "name";
	private static final String KEY_TRACKS_NOTES     = "notes";
	private static final String KEY_TRACKS_START_ID  = "startid";
	
	// Contacts Table Columns names
	private static final String KEY_TRACKS_ENDS_ID   = "id";
	private static final String KEY_TRACKS_ENDS_NAME = "name";
	private static final String KEY_TRACKS_END_LAST_ID    = "endid";
		
	private String actualTrackName = null;
	
	private SQLiteDatabase db  = null;
	

	public DBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		db = getWritableDatabase();
	}

	
	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SENSOR_VALUES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_TIME    + " BIGINT,"
				+ KEY_TILT    + " FLOAT,"
				+ KEY_ROLL    + " FLOAT,"
				+ KEY_HEADING + " FLOAT,"
				+ KEY_ACC_X   + " FLOAT," 
				+ KEY_ACC_Y   + " FLOAT,"
				+ KEY_ACC_Z   + " FLOAT" + ")";
		
		String CREATE_TRACKS_TABLE = "CREATE TABLE " + TABLE_TRACKS + "("
				+ KEY_TRACKS_ID + " INTEGER PRIMARY KEY," 
				+ KEY_TRACKS_NAME    + " TEXT,"
				+ KEY_TRACKS_NOTES    + " TEXT,"
				+ KEY_TRACKS_START_ID    + " INTEGER" + ")";
		
		db.execSQL(CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_TRACKS_TABLE);
	}

	
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR_VALUES);
		// Create tables again
		onCreate(db);
	}
	
	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	public void addSensorValue(SensorsValue value) {

		ContentValues values = new ContentValues();
		values.put(KEY_TIME,    value.getTime()); 
		values.put(KEY_TILT,    value.getTilt()); 
		values.put(KEY_ROLL,    value.getRoll()); 
		values.put(KEY_HEADING, value.getHeading()); 
		values.put(KEY_ACC_X,   value.getAccX()); 
		values.put(KEY_ACC_Y,   value.getAccY()); 
		values.put(KEY_ACC_Z,   value.getAccZ()); 

		db.insert(TABLE_SENSOR_VALUES, null, values);
		

	}



	
	// Getting All Contacts
	public ArrayList<SensorsValue> getAllValues() {
		ArrayList<SensorsValue> sensorValues = new ArrayList<SensorsValue>();
		
		Log.d("getAllContacts", "calledddd");
		
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_SENSOR_VALUES;

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				SensorsValue value = new SensorsValue();
				value.setId  (Integer.parseInt(cursor.getString(0)));
				value.setTime(Long.parseLong(cursor.getString(1)));
				
				value.setTilt(Float.parseFloat(cursor.getString(2)));
				value.setRoll(Float.parseFloat(cursor.getString(3)));
				value.setHeading(Float.parseFloat(cursor.getString(4)));

				value.setAccX(Float.parseFloat(cursor.getString(5)));
				value.setAccY(Float.parseFloat(cursor.getString(6)));
				value.setAccZ(Float.parseFloat(cursor.getString(7)));
				
				//Log.d("getAllContacts",cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3) + " " + cursor.getString(4) );
				
				// Adding contact to list
				sensorValues.add(value);
			} while (cursor.moveToNext());
		}

		// return contact list
		return sensorValues;
	}

	
	public ArrayList<String> getTracksNames(){
		ArrayList<String> names = new ArrayList<String>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TRACKS;

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {			
				names.add(cursor.getString(1));
			} while (cursor.moveToNext());
		}

		// return contact list
		return names;		
	}
	
	
	
	
	public boolean addNewTrack(String name, String notes){
		//controllo se c'e' gia il nome
		ArrayList<String> nomi = getTracksNames();
		
		for(int i=0; i<nomi.size(); i++){
			if(nomi.get(i).equals(name))
				return false;
		}
		
		actualTrackName = name;
		int lastId = (int) getLastSensorValueId();
		String mNotes = (notes == null) ? "" : notes;
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_TRACKS_NAME,     name); 
		values.put(KEY_TRACKS_NOTES,    mNotes); 
		values.put(KEY_TRACKS_START_ID, lastId); 

		// Inserting Row
		db.insert(TABLE_TRACKS, null, values);
		
		return true;
	}
	
	public boolean addTrackEnd(){
		
		if(actualTrackName == null)
			return false;

		int lastId = (int) getLastSensorValueId();
		
		ContentValues values = new ContentValues();
		values.put(KEY_TRACKS_ENDS_NAME,     actualTrackName); 
		values.put(KEY_TRACKS_END_LAST_ID, lastId); 

		// Inserting Row
		db.insert(TABLE_TRACKS_ENDS, null, values);
		
		actualTrackName = null;
		
		return true;
	}
	
	
	
	public void closeDb(){
		db.close();
	}
	
	
	public long getLastSensorValueId(){

		addSensorValue(new SensorsValue()); 	
			
		String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_SENSOR_VALUES + " order by " + KEY_ID +" DESC LIMIT 1";
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor != null && cursor.moveToFirst()) {
		    return cursor.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
		}
		else{
			return -1;
		}	
	}
	

	// Getting contacts Count
	public int getContactsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_SENSOR_VALUES;
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}
		
		
		
}

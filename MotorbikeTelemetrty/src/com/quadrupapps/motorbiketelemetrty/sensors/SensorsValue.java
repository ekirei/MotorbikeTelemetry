package com.quadrupapps.motorbiketelemetrty.sensors;

public class SensorsValue {

	private int id = 0;
	private long time = -1;
	private float tilt = 0;
	private float roll = 0;
	private float heading = 0;
	private float accX = 0;
	private float accY = 0;
	private float accZ = 0;
	
	
	public SensorsValue(){	
	}
	
	public SensorsValue(int id, long time, float tilt, float roll,
			float heading, float accX, float accY, float accZ) {
		super();
		this.id = id;
		this.time = time;
		this.tilt = tilt;
		this.roll = roll;
		this.heading = heading;
		this.accX = accX;
		this.accY = accY;
		this.accZ = accZ;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public float getTilt() {
		return tilt;
	}
	public void setTilt(float tilt) {
		this.tilt = tilt;
	}
	public float getRoll() {
		return roll;
	}
	public void setRoll(float roll) {
		this.roll = roll;
	}
	public float getHeading() {
		return heading;
	}
	public void setHeading(float heading) {
		this.heading = heading;
	}
	public float getAccX() {
		return accX;
	}
	public void setAccX(float accX) {
		this.accX = accX;
	}
	public float getAccY() {
		return accY;
	}
	public void setAccY(float accY) {
		this.accY = accY;
	}
	public float getAccZ() {
		return accZ;
	}
	public void setAccZ(float accZ) {
		this.accZ = accZ;
	}
	
	
	
	
}

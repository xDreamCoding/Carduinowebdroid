package de.carduinodroid.shared;

public class GPS {

	private Object datetime;
	private int driverID;
	private String longitude;
	private String latitude;
	
	public GPS(int driveID, String longi, String lati,Object time){
		datetime = time;
		driverID = driveID;
		longitude = longi;
		latitude = lati;
	}

	public String getLongitude(){
		return longitude;
	}
	
	public String getLatitude(){
		return latitude;
	}
	
	public int getdriveID(){
		return driverID;
	}

	public Object getdateTime(){
		return datetime;
	}
}

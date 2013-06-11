package de.carduinodroid.utilities;

import de.carduinodroid.desktop.Controller.Controller_Computer;
import de.carduinodroid.desktop.Model.GPSTrack;

public class CarControllerWrapper {
	Controller_Computer cc;
	int speed, angle;
	
	public CarControllerWrapper(LogNG log, GPSTrack gps) {
		cc = new Controller_Computer(log, this, gps);
	}

	public void confirmButtonUp(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void confirmButtonDown(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void confirmButtonRight(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void confirmButtonLeft(boolean b) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * @return the angle
	 */
	public int getAngle() {
		return angle;
	}

	/**
	 * @param angle the angle to set
	 */
	public void setAngle(int angle) {
		this.angle = angle;
	}
}

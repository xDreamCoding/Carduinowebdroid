package de.carduinodroid.utilities;

import java.awt.image.BufferedImage;

import de.carduinodroid.desktop.Controller.Controller_Computer;
import de.carduinodroid.desktop.Model.GPSTrack;

public class CarControllerWrapper {
	Controller_Computer cc;
	int speed, angle;
	BufferedImage img;
	String[] resolutions;

	public CarControllerWrapper(LogNG log) {
		cc = new Controller_Computer(log, this);		
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

	/**
	 * @return the img
	 */
	public BufferedImage getImg() {
		return img;
	}

	/**
	 * @param img the img to set
	 */
	public void setImg(BufferedImage img) {
		this.img = img;
	}	
	
	/**
	 * @return the resolutions
	 */
	public String[] getResolutions() {
		return resolutions;
	}

	/**
	 * @param resolutions the resolutions to set
	 */
	public void setResolutions(String[] resolutions) {
		this.resolutions = resolutions;
	}
}

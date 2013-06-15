package de.carduinodroid.utilities;

import java.awt.image.BufferedImage;

import de.carduinodroid.desktop.Controller.Controller_Computer;

/**
 * \brief Wrapper to combine the given source code in one class.
 * \details Since the code in de.carduinodroid.desktop was given to us we created this function to have one interface to interfere with it.
 * \details This is implemented as a Singleton.
 * @author Michael Röding
 *
 */
public class CarControllerWrapper {
	static CarControllerWrapper ccw = null;
	
	Controller_Computer cc;
	int speed, angle;
	BufferedImage img;
	String[] resolutions;
	String latitude, longitude;

	private CarControllerWrapper(LogNG log) {
		cc = new Controller_Computer(log, this);		
	}
	
	/**
	 * \brief Use this contructor for first time instancing.
	 * \details if necessary it creates the static CarControllerWrapper object.
	 * \datails If this function was called once use getCarController() instead.
	 * @param log Log for logging
	 * @return Returns the static CarControllerWrapper object
	 */
	public static CarControllerWrapper getCarController(LogNG log) {
		if(ccw == null)
			ccw = new CarControllerWrapper(log);
		return ccw;
	}
	
	/**
	 * \brief Use this function to access the static CarControllerWrapper object.
	 * @return Returns the static CarControllerWrapper object.
	 * @throws Exception In case the static CarControllerWrapper object is not present.
	 */
	public static CarControllerWrapper getCarController() throws Exception {
		if(ccw == null)
			throw new Exception("wrong contructor for first time instancing");
		return ccw;
	}
	
	/**
	 * \brief Send controll signal to the car
	 * @param up
	 * @param down
	 * @param right
	 * @param left
	 */
	public void setDirection(boolean up, boolean down, boolean right, boolean left) {
		ccw.cc.car_controller.UpdateVariables(up, down, right, left);
	}
	
	public void driveForward() { setDirection(true, false, false, false); }
	public void driveBackward() { setDirection(false, true, false, false); }
	public void driveRight() { setDirection(false, false, true, false); }
	public void driveLeft() { setDirection(false, false, false, true); }

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
	 * \brief Returns the current max speed of the car.
	 * @return Returns the current max speed of the car.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * \brief Sets the current max speed of the car.
	 * @param speed The speed to sets
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	/**
	 * \brief Returns the current steering angle of the car.
	 * @return Returns the current steering angle of the car.
	 */
	public int getAngle() {
		return angle;
	}

	/**
	 * \brief Sets the current steering angle of the car.
	 * @param angle The angle to set.
	 */
	public void setAngle(int angle) {
		this.angle = angle;
	}

	/**
	 * \brief Returns the current image.
	 * @return Returns the current image.
	 */
	public BufferedImage getImg() {
		return img;
	}

	/**
	 * \brief Sets the current image.
	 * @param img The img to set.
	 */
	public void setImg(BufferedImage img) {
		this.img = img;
	}	
	
	/**
	 * \brief Returns all possible resolutions.
	 * @return Returns all possible resolutions as a String array
	 */
	public String[] getResolutions() {
		return resolutions;
	}

	/**
	 * \brief Sets the all possible resolutions.
	 * @param resolutions The resolutions to set.
	 */
	public void setResolutions(String[] resolutions) {
		this.resolutions = resolutions;
	}

	/**
	 * \brief Returns the current latitude.
	 * @return Returns the current latitude or null if not available.
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * \brief Sets the current position (latitude) of the car.
	 * @param latitude The latitude to set.
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * \brief Returns the current longitude.
	 * @return Returns the current longitude or null if not available.
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * \brief Sets the current position (longitude) of the car.
	 * @param longitude The longitude to set.
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}

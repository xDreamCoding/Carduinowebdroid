package de.carduinodroid.utilities;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import de.carduinodroid.MyWebSocketServlet;
import de.carduinodroid.desktop.Controller.Controller_Computer;

/**
 * \brief Wrapper to combine the given source code in one class. \details Since
 * the code in de.carduinodroid.desktop was given to us we created this function
 * to have one interface to interact with it. \details This is implemented as a
 * Singleton.
 * 
 * @author Michael Röding
 * 
 */
public class CarControllerWrapper {
	static CarControllerWrapper ccw = null;

	Controller_Computer cc;
	int speed, angle;
	BufferedImage img, oldImage;
	String[] resolutions;
	String latitude, longitude;
	static boolean up = false, down = false, right = false, left = false,
			lightOn = false, connected = false;
	static int sameImage = 0;

	private CarControllerWrapper(Log log) {
		cc = new Controller_Computer(log, this);
	}

	/**
	 * \brief Use this constructor for first time instancing. \details if
	 * necessary it creates the static CarControllerWrapper object. \details If
	 * this function was called once use getCarController() instead.
	 * 
	 * @param log
	 *            Log for logging.
	 * @return Returns the static CarControllerWrapper object.
	 */
	public static CarControllerWrapper getCarController(Log log) {
		if (ccw == null) {
			ccw = new CarControllerWrapper(log);
		}
		return ccw;
	}

	/**
	 * \brief Use this function to access the static CarControllerWrapper
	 * object.
	 * 
	 * @return Returns the static CarControllerWrapper object.
	 * @throws Exception
	 *             in case the static CarControllerWrapper object is not
	 *             present.
	 */
	public static CarControllerWrapper getCarController() throws Exception {
		if (ccw == null)
			throw new Exception("wrong constructor for first time instancing");
		return ccw;
	}

	/**
	 * \brief Connects the server with the carduinodroid.
	 * 
	 * @param ip
	 *            IP address to connect to.
	 * @throws UnknownHostException
	 *             In case the ip address is invalid or the carduinodroid is
	 *             unreachable.
	 */
	public void connect(String ip) throws Exception {
		if (ccw == null)
			throw new NullPointerException();

		ccw.cc.network.connect(ip);
		connected = true;
	}

	/**
	 * \brief Update Variables in Car_Controller
	 */
	public static void updateDirection() {
		if (connected)
			ccw.cc.car_controller.UpdateVariables(up, down, right, left);
	}

	/**
	 * \brief Turns the light on or off.
	 * 
	 * @param on
	 *            Whether light is turned on or no.
	 */
	private static void setLight(boolean on) {
		if (connected)
			ccw.cc.camera_settings.send_switch_light(on ? "1" : "0");
	}

	/**
	 * \brief Toggles light on and off
	 */
	public static void toggleLight() {
		lightOn = !lightOn;
		setLight(lightOn);
	}

	/**
	 * \brief Sends an audio signal
	 */
	public static void sendSignal() {
		if (connected)
			ccw.cc.sound_output.send_output_soundsignal("1");
	}

	public static void setUp(boolean bool) {
		up = bool;
		updateDirection();
	}

	public static void setDown(boolean bool) {
		down = bool;
		updateDirection();
	}

	public static void setRight(boolean bool) {
		right = bool;
		updateDirection();
	}

	public static void setLeft(boolean bool) {
		left = bool;
		updateDirection();
	}

	/**
	 * \brief Returns the current max speed of the car.
	 * 
	 * @return Returns the current max speed of the car.
	 */
	public static int getSpeed() {
		return ccw.speed;
	}

	/**
	 * \brief Sets the current max speed of the car.
	 * 
	 * @param speed
	 *            The speed to set.
	 */
	public static void setSpeed(int speed) {
		ccw.speed = speed;
	}

	/**
	 * \brief Returns the current steering angle of the car.
	 * 
	 * @return Returns the current steering angle of the car.
	 */
	public static int getAngle() {
		return ccw.angle;
	}

	/**
	 * \brief Sets the current steering angle of the car.
	 * 
	 * @param angle
	 *            The angle to set.
	 */
	public static void setAngle(int angle) {
		ccw.angle = angle;
	}

	/**
	 * \brief Returns the current image.
	 * 
	 * @return Returns the current image.
	 */
	public static BufferedImage getImg() {
		return ccw.img;
	}

	/**
	 * \brief Sets the current image.
	 * 
	 * @param img
	 *            The image to set.
	 */
	public void setImg(BufferedImage img) {
		ccw.img = img;
	}

	public void sendImg(BufferedImage before) {
		int w = before.getWidth() / 2;
		int h = before.getHeight() / 2;
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(0.5, 0.5);
		AffineTransformOp scaleOp = 
		   new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		image = scaleOp.filter(before, image);
		int H;
		int B;

		/* Test */
		// File file = new
		// File("C:\\Users\\Michael\\Pictures\\im_super_serial__2.png");
		// System.out.println("Datei gefunden, Laenge = " + file.length());
		// ByteBuffer bbu = ByteBuffer.allocate((int) file.length());
		// System.out.println("ByteBuffer erzeugt");
		// final int BYTES_PER_READ = (int) file.length();
		// System.out.println("Bytes ermittelt");
		// FileInputStream fis = new
		// FileInputStream("C:\\Users\\Michael\\Pictures\\im_super_serial__2.png");
		// System.out.println("Inputstream erstellt");
		// int bytesRead = 0;
		// byte[] buf = new byte[BYTES_PER_READ];
		// System.out.println("Buf erzeugt");
		// while (bytesRead != -1)
		// {
		// bbu.put(buf, 0, bytesRead);
		// bytesRead = fis.read(buf);
		// System.out.println(" " + bytesRead);
		// }
		// System.out.println("Bytes kopiert");
		// fis.close();
		//
		// BufferedImage image = ImageIO.read( file);
		// B = image.getWidth();
		// H = image.getHeight();
		/* Test */
		/* Work */

		if (!image.equals(oldImage)) {
			oldImage = image;
			B = image.getWidth();
			H = image.getHeight();
			/* work */

			System.out.println("Bild gelesen, Breite = " + B + " Höhe = " + H);
			// Grösse von pixels = Breite * Höhe * 4 bytes
			int[] pixels = new int[B * H];
			int BufLeange = B * H * 4 + 8; // 8 bytes fuer Breite und Hoehe
			System.out.println("pixels angelegt");
			// kopiert alle Pixel von image ( oder nur Bildausschnitt) in ein
			// Feld ( pixels)
			image.getRGB(0, 0, B, H, pixels, 0, B);
			System.out.println("Bildgröße bestimmt und RGB- Image erzeugt");

			System.out.println("Daten für ByteBuffer: " + B * H);
			ByteBuffer buffer = ByteBuffer.allocate(BufLeange);
			buffer.clear();
			System.out.println("ByteBuffer erstellt = " + BufLeange + " byte");

			// Bildgroesse im Puffer hinterlegen
			// Bild- Breite int -> byte[]
			buffer.put((byte) (B >>> 24));
			buffer.put((byte) (B >>> 16));
			buffer.put((byte) (B >>> 8));
			buffer.put((byte) (B));
			System.out.println("Bildbreite in ByteBuffer");

			// Bild- Hoehe int -> byte[]
			buffer.put((byte) (H >>> 24));
			buffer.put((byte) (H >>> 16));
			buffer.put((byte) (H >>> 8));
			buffer.put((byte) (H));
			System.out.println("Bildhöhe in ByteBuffer");

			// Bild Pixelinformationen in Puffer hinterlegen
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int pixel = pixels[y * image.getWidth() + x];
					buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
					buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
					buffer.put((byte) (pixel & 0xFF)); // Blue component
//					buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
																// component.
																// nicht bei
																// BufferdImage
				}
			}

			buffer.flip(); // ByteBuffer speichern
			System.out.println("ByteBuffer geschrieben");

			MyWebSocketServlet.broadcastImage(buffer);
		} else {
			System.out.println("Same image [" + ++sameImage
					+ "] -> not sending");
		}
	}

	/**
	 * \brief Returns all possible resolutions.
	 * 
	 * @return Returns all possible resolutions as a String array.
	 */
	public static String[] getResolutions() {
		return ccw.resolutions;
	}

	/**
	 * \brief Sets the possible resolutions.
	 * 
	 * @param resolutions
	 *            The resolutions to set.
	 */
	public void setResolutions(String[] resolutions) {
		ccw.resolutions = resolutions;
	}

	/**
	 * \brief Returns the current latitude.
	 * 
	 * @return Returns the current latitude or null if not available.
	 */
	public static String getLatitude() {
		return ccw.latitude;
	}

	/**
	 * \brief Sets the current position (latitude) of the car.
	 * 
	 * @param latitude
	 *            The latitude to set.
	 */
	public void setLatitude(String latitude) {
		ccw.latitude = latitude;
	}

	/**
	 * \brief Returns the current longitude.
	 * 
	 * @return Returns the current longitude or null if not available.
	 */
	public static String getLongitude() {
		return ccw.longitude;
	}

	/**
	 * \brief Sets the current position (longitude) of the car.
	 * 
	 * @param longitude
	 *            The longitude to set.
	 */
	public void setLongitude(String longitude) {
		ccw.longitude = longitude;
	}
}

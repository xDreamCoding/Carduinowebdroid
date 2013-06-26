package de.carduinodroid;

import java.io.IOException;
import java.nio.CharBuffer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.carduinodroid.shared.*;
import de.carduinodroid.shared.User.Right;
import de.carduinodroid.utilities.*;
import de.carduinodroid.utilities.Config.Options;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.Timer;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * Servlet implementation class Main
 */
//@WebServlet(loadOnStartup=1, value = "/main")

/**
 * \brief This Class is used to handle user-timeouts and to log GPS in a given interval and to handle the waiting queue
 * @author Alexander Rose
 *
 */

public class Main /* extends HttpServlet */ {
	private static Timer caretaker;
	private static Timer GPSLog;
	private static TimerTask GPSLogger;
	private static TimerTask Session;
	private static TimerTask action;
	private static boolean aliveSessions;
	static Log log;
	static int driveID;
	static int Fahrzeit, gpsLogInterval;
	static Options opt;
	static boolean flag;
	static String aktSessionID;
    
	final static boolean DEBUG = false;
	
	/** 
	 * \brief refreshes the options needed in this class (Fahrzeit and GPS interval)
	 * @param opt Options object
	 */
	
	public static void refresh(Options opt) {
    	Fahrzeit = opt.fahrZeit;
    	gpsLogInterval = opt.logGPSInterval;
    	flag = true;
    }
	
	/** 
	 * \brief deletes all Timers and TimerTasks and also deletes all active sessions
	 */
	
	public static void shutDown() {
		if(DEBUG) System.out.println("Shut-Down main");
		Session.cancel();
		action.cancel();
		GPSLogger.cancel();
		GPSLog.cancel();
		activeSession.deleteAll();
	}
    
    public static void restartTimer() {
    	caretaker.cancel();
    	caretaker = new Timer();
    	caretaker.schedule(new de.carduinodroid.Dummy(action), 1000, 60000*Fahrzeit);		
    }
	
	public static void receivedPing(String SessionID){
		aliveSessions = false;
	}
    
    public static void main(Options opt, DBConnector db, Log logng) {
    	
    	log = logng;
    	aliveSessions = false;
    	
    	Session = new TimerTask(){
			public void run(){
							
				if (aliveSessions){
					activeSession.deleteSession(activeSession.getDriver());
				}									
				aliveSessions = true;			   			
			}
		};
    	
		Timer Sessionhandle = new Timer();
		Sessionhandle.schedule(Session, 10, 5000);
		Fahrzeit = opt.fahrZeit;
		gpsLogInterval = opt.logGPSInterval;
		if(DEBUG) System.out.println("Main-function");
		
		try {
			action = new TimerTask() {
				DBConnector db = new DBConnector();
				public void run() {					
					if(flag){
			    		caretaker.cancel();
						caretaker = new Timer();
			    		caretaker.schedule(new de.carduinodroid.Dummy(action), 60000*Fahrzeit, 60000*Fahrzeit);
			    		flag = false;
			    	}
					
			    	activeSession.resetDriver();
			    	
					if (waitingqueue.isEmpty() == true){
						caretaker.cancel();
						caretaker = new Timer();
						caretaker.schedule(new de.carduinodroid.Dummy(action), 1000, 60000*Fahrzeit);				
						return;
					}
					else{
						try {
							if(aktSessionID != null) 
								activeSession.getSocket(aktSessionID).writeTextMessage(CharBuffer.wrap(MyWebSocketServlet.identifierControl + "n"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							String aktSessionID = waitingqueue.getNextUser();
							driveID = db.startDrive(db.getUserIdBySession(activeSession.getSessionInt(aktSessionID)));
							activeSession.setDriver(aktSessionID);
							activeSession.getSocket(aktSessionID).writeTextMessage(CharBuffer.wrap(MyWebSocketServlet.identifierControl + "y"));
							///TODO \todo Fahrrechte;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						}
					}
			    
			};
		} catch (Exception e) {
			log.writelogfile(e.getMessage());
			e.printStackTrace();
		}
	
		
		
		caretaker = new Timer();
		caretaker.schedule(action, 100, 60000*Fahrzeit);  
		
		try {
			GPSLogger = new TimerTask() {		
				public void run(){
					String longitude = CarControllerWrapper.getLongitude();
					String latitude = CarControllerWrapper.getLatitude();
					if (longitude == null || latitude == null){
						if(DEBUG) System.out.println("GPS: N/A");
					} else
						log.logGPS(driveID, longitude, latitude);
				}
			};
		} catch (Exception e) {
			log.writelogfile(e.getMessage());
			e.printStackTrace();
		}
		GPSLog = new Timer();
		GPSLog.schedule(GPSLogger, 10, gpsLogInterval * 1000);
    }   
    
}

package de.carduinodroid.shared;

import java.util.ArrayList;

import de.carduinodroid.Main;
import de.carduinodroid.utilities.CarControllerWrapper;
import de.carduinodroid.utilities.DBConnector;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Session;
import org.apache.catalina.websocket.WsOutbound;

/**
 * \brief This Class is used to store all active sessions and provides access to them
 * @author Alexander ROse
 *
 */

public class activeSession {

	private static ArrayList<String> activeSessions;
	private static ArrayList<Integer> activeInt;
	private static ArrayList<WsOutbound> activeSocket;
	private static ArrayList<HttpSession> activeTomcat;
	private static int Driver;
	static DBConnector db;
	
	/** 
	 * \brief initializes the activeSession queue
	 */
	
	public static void init(){
		activeSessions = new ArrayList<String>();
		activeInt = new ArrayList<Integer>();
		activeSocket = new ArrayList<WsOutbound>();
		activeTomcat = new ArrayList<HttpSession>();
		db = null;
		Driver = -1;
		try {
			db = new DBConnector();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}
	
	/** 
	 * \brief inserts a session into the queue and also creates a session in the DB
	 * @param SessionID Tomcat session of the user
	 * @param ipadress of the user (ipv4 and ipv6 are possible)
	 * @param userid of the user
	 */
	
	public static int insertSession(String SessionID,String ipadress,String userid){
		if (activeSessions.contains(SessionID)){
			System.out.println("bereits verbunden");
			return -1;
		}
		int ID = -1;		
		ID = db.createSession(userid, ipadress);
		if (ID == -1){
			System.out.println("konnte Session nicht erstellen");
			return -1;
		}
		activeSessions.add(SessionID);
		activeInt.add(ID);
		activeSocket.add(null);
		
		return ID;
	}
	
	/** 
	 * @return Returns all SessionID's in the queue
	 */
	
	public static String[] getAllSessions(){
		String[] AllSessions = new String[activeSessions.size()];
		for(int i = 0; i < AllSessions.length; i++){
			AllSessions[i] = activeSessions.get(i);
		}
		return AllSessions;
	}
	
	/** 
	 * \brief removes a session from the queue and closes the DB Session
	 * @param SessionID of the user
	 */
	
	public static void deleteSession(String SessionID){
		int index = activeSessions.indexOf(SessionID);
		if (index == -1){
			//System.out.println("Session bereits gelöscht");
			return;
		}
		db.closeSession(getSessionInt(SessionID));
		activeSessions.remove(index);
		activeInt.remove(index);
		activeSocket.remove(index);
		
		if (index < Driver){
			Driver = Driver -1;
		}
		else{
			if (index == Driver){
				db.stopDrive(activeInt.get(Driver));
				Driver = -1;
				Main.restartTimer();
				CarControllerWrapper.setDown(false);
				CarControllerWrapper.setLeft(false);
				CarControllerWrapper.setRight(false);
				CarControllerWrapper.setUp(false);
			}
		}			
		if (activeSessions.size() == 0){
			deleteAll();
		}
	}
	
	/** 
	 * \brief removes all sessions from the queue and closes all DB sessions
	 */
	
	public static void deleteAll(){
		for (int i = 0; i < activeSessions.size(); i++){
			db.closeSession(getSessionInt(activeSessions.get(i)));
		}
		
		activeSessions.clear();
		activeInt.clear();	
		activeSocket.clear();
		Driver = -1;
	}

	/** 
	 * \brief returns the SessionID of the DB which belongs to the given Tomcat SessionID
	 * @param SessionID from Tomcat
	 * @return Returns SessionID from DB
	 */
	
	public static int getSessionInt(String SessionID){
		if (SessionID == null) return -1;
		int SessionInt = activeInt.get(activeSessions.indexOf(SessionID));
		return SessionInt;
	}
	
	//debug Funktion
	public static String getSession(String SessionID){
		int Session = getSessionInt(SessionID);
		int index = activeInt.indexOf(Session);
		String debug = activeSessions.get(index);
		return debug;
	}

	/** 
	 * \brief saves a Socket for a specific User
	 * @param SessionID of the User
	 * @param sock Socket
	 */
	
	public static void insertSocket(String SessionID, WsOutbound sock){
		int index = activeSessions.indexOf(SessionID);
		if (index == -1){
			return;
		}
		activeSocket.set(index, sock);
	}

	/** 
	 * \brief delete a Socket from a specific User
	 * @param SessionID of the User
	 */
	
	public static void deleteSocket(String SessionID){
		int index = activeSessions.indexOf(SessionID);
		if (index == -1){
			System.out.println("Zugehörige Session bereits gelöscht");
			return;
		}
		activeSocket.set(index, null);
	}

	/** 
	 * \brief find out if the SessionID belongs to the current Driver
	 * @param SessionID of the User
	 * @return true if its the Driver else false
	 */
	
	public static boolean isDriver(String SessionID){
		int index = activeSessions.indexOf(SessionID);
		if (index == -1){
			return false;
		}
		boolean isDriver = (index == Driver);
		return isDriver;
	}

	/** 
	 * \brief mark the given User as Driver (if someone else was Diver before, he is no longer the Driver)
	 * @param SessionID of the User
	 */
	
	public static void setDriver(String SessionID){
		if (!(Driver == -1)){
			db.stopDrive(activeInt.get(Driver));
		}
		int index = activeSessions.indexOf(SessionID);
		Driver = index;
	}

	/** 
	 * \brief nobody is Driver after u call this function
	 */
	
	public static void resetDriver(){
		if (!(Driver == -1)){
			db.stopDrive(activeInt.get(Driver));
		}
		Driver = -1;
	}

	/** 
	 * \brief returns true if the activeSessions-queue contains the given SessionID
	 * @param SessionID of the User 
	 */
	
	public static boolean isActive(String SessionID){
		return activeSessions.contains(SessionID);
	}

	public static WsOutbound getSocket(String SessionID){
		int index = activeSessions.indexOf(SessionID);
		if (index == -1){
			return null;
		}
		return activeSocket.get(index);
	}

	public static String getDriver(){
		if (Driver == -1) return null;
		return activeSessions.get(Driver);
	}

	public static int getLength(){
		return activeSessions.size();
	}
}

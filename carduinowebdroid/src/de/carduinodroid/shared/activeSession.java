package de.carduinodroid.shared;

import java.util.ArrayList;

import de.carduinodroid.QueueManager;
import de.carduinodroid.utilities.CarControllerWrapper;
import de.carduinodroid.utilities.DBConnector;
import javax.servlet.http.HttpSession;
import org.apache.catalina.websocket.WsOutbound;

/**
 * \brief This Class is used to store all active sessions and provides access to them
 * @author Alexander ROse
 *
 */

public class activeSession {

	private static ArrayList<HttpSession> activeTomcat;
	private static int Driver;
	private static int DriverID;
	static DBConnector db;
	
	/** 
	 * \brief initializes the activeSession queue
	 */
	
	public static void init(){
		activeTomcat = new ArrayList<HttpSession>();
		db = null;
		Driver = -1;
		DriverID = -1;
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
	
	public static int insertSession(String ipadress,String userid, HttpSession Tomcat){
		
		if (activeTomcat.contains(Tomcat)){
			System.out.println("bereits verbunden");
			return -1;
		}
		int ID = -1;		
		ID = db.createSession(userid, ipadress);
		if (ID == -1){
			System.out.println("konnte Session nicht erstellen");
			return -1;
		}
		Tomcat.setAttribute("DBID", ID);
		Tomcat.setAttribute("Socket", null);
		activeTomcat.add(Tomcat);
		
		return ID;
	}
	
	/** 
	 * @return Returns all SessionID's in the queue
	 */
	
	public static HttpSession[] getAllSessions(){
		HttpSession[] AllSessions = new HttpSession[activeTomcat.size()];
		for(int i = 0; i < AllSessions.length; i++){
			AllSessions[i] = activeTomcat.get(i);
		}
		return AllSessions;
	}
	
	/** 
	 * \brief removes a session from the queue and closes the DB Session
	 * @param SessionID of the user
	 */
	
	public static void deleteSession(HttpSession Session){
		int index = activeTomcat.indexOf(Session);
		if (index == -1){
			//System.out.println("Session bereits gelöscht");
			return;
		}
		db.closeSession((int)Session.getAttribute("DBID"));
		Session.removeAttribute("nickName");
		Session.removeAttribute("DBID");
		Session.removeAttribute("Socket");
		activeTomcat.remove(index);
		
		if (index < Driver){
			Driver = Driver -1;
		}
		else{
			if (index == Driver){
				db.stopDrive(DriverID);
				Driver = -1;
				QueueManager.restartTimer();
				CarControllerWrapper.setDown(false);
				CarControllerWrapper.setLeft(false);
				CarControllerWrapper.setRight(false);
				CarControllerWrapper.setUp(false);
			}
		}			
		if (activeTomcat.size() == 0){
			deleteAll();
		}
	}
	
	/** 
	 * \brief removes all sessions from the queue and closes all DB sessions
	 */
	
	public static void deleteAll(){
		for (int i = 0; i < activeTomcat.size(); i++){
			HttpSession Session = activeTomcat.get(i);
			try{
				db.closeSession((int)Session.getAttribute("DBID"));
				Session.removeAttribute("nickName");
				Session.removeAttribute("Socket");
				Session.removeAttribute("DBID");
			}
			catch(IllegalStateException ie){
				System.out.println("Session bereits teilweise oder ganz entfernt");
			}
		}
		
		activeTomcat.clear();
		Driver = -1;
	}

	/** 
	 * \brief returns the SessionID of the DB which belongs to the given Tomcat SessionID
	 * @param SessionID from Tomcat
	 * @return Returns SessionID from DB
	 */
	
//	public static int getSessionInt(String SessionID){
//		if (SessionID == null) return -1;
//		int SessionInt = activeInt.get(activeSessions.indexOf(SessionID));
//		return SessionInt;
//	}
	
	//debug Funktion
	public static HttpSession getSession(String SessionID){
		int index = -1;
		for (int i = 0; i < activeTomcat.size(); i++){
			if (activeTomcat.get(i).getId().equals(SessionID)){
				index = i;
				break;
			}
		}
		if (index == -1){
			return null;
		}
		HttpSession Session = activeTomcat.get(index);
		return Session;
	}

	public static HttpSession getSession(int SessionID){
		int index = -1;
		for (int i = 0; i < activeTomcat.size(); i++){
			if ((int)activeTomcat.get(i).getAttribute("DBID") == (SessionID)){
				index = i;
				break;
			}
		}
		if (index == -1){
			return null;
		}
		HttpSession Session = activeTomcat.get(index);
		return Session;
	}
	/** 
	 * \brief saves a Socket for a specific User
	 * @param SessionID of the User
	 * @param sock Socket
	 */
	
	public static void insertSocket(HttpSession Session, WsOutbound sock){
		int index = activeTomcat.indexOf(Session);
		if (index == -1){
			return;
		}
		Session.setAttribute("Socket", sock);
	}

	/** 
	 * \brief delete a Socket from a specific User
	 * @param SessionID of the User
	 */
	
	public static void deleteSocket(HttpSession Session){
		int index = activeTomcat.indexOf(Session);
		if (index == -1){
			System.out.println("Zugehörige Session bereits gelöscht");
			return;
		}
		Session.removeAttribute("Socket");
	}

	/** 
	 * \brief find out if the SessionID belongs to the current Driver
	 * @param SessionID of the User
	 * @return true if its the Driver else false
	 */
	
	public static boolean isDriver(HttpSession Session){
		int index = activeTomcat.indexOf(Session);
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
	
	public static void setDriver(HttpSession Session, int driverID){
		if (!(Driver == -1)){
			db.stopDrive(DriverID);
		}
		int index = activeTomcat.indexOf(Session);
		Driver = index;
		DriverID = driverID;
	}

	/** 
	 * \brief nobody is Driver after u call this function
	 */
	
	public static void resetDriver(){
		if (!(Driver == -1)){
			db.stopDrive(DriverID);
		}
		Driver = -1;
		DriverID = -1;
	}

	/** 
	 * \brief returns true if the activeSessions-queue contains the given SessionID
	 * @param SessionID of the User 
	 */
	
	public static boolean isActive(HttpSession Session){
		return activeTomcat.contains(Session);
	}

	public static WsOutbound getSocket(HttpSession Session){
		int index = activeTomcat.indexOf(Session);
		if (index == -1){
			return null;
		}
		return (WsOutbound)activeTomcat.get(index).getAttribute("Socket");
	}

	public static HttpSession getDriver(){
		if (Driver == -1) return null;
		return activeTomcat.get(Driver);
	}

	public static int getLength(){
		return activeTomcat.size();
	}
}

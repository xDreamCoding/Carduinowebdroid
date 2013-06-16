package de.carduinodroid.shared;

import java.util.ArrayList;
import de.carduinodroid.utilities.DBConnector;
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
	private static int Driver;
	static DBConnector db;
	
	/** 
	 * \brief initializes the activeSession queue
	 */
	
	public static void init(){
		activeSessions = new ArrayList<String>();
		activeInt = new ArrayList<Integer>();
		activeSocket = new ArrayList<WsOutbound>();
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
	
	public static void insertSession(String SessionID,String ipadress,String userid){
		int ID = -1;		
		ID = db.createSession(userid, ipadress);
		if (ID == -1){
			System.out.println("konnte Session nicht erstellen");
			return;
		}
		activeSessions.add(SessionID);
		activeInt.add(ID);
		activeSocket.add(null);
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
			System.out.println("Session bereits gel�scht");
			return;
		}
		db.closeSession(getSessionInt(SessionID));
		activeSessions.remove(index);
		activeInt.remove(index);
		activeSocket.remove(index);
		
		if (index < Driver){
			Driver = Driver -1;
		}
		if (index == Driver){
			Driver = -1;
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

	public static void insertSocket(String SessionID, WsOutbound sock){
		int index = activeSessions.indexOf(SessionID);
		activeSocket.set(index, sock);
	}

	public static void deleteSocket(String SessionID){
		int index = activeSessions.indexOf(SessionID);
		activeSocket.set(index, null);
	}

	public static boolean isDriver(String SessionID){
		int index = activeSessions.indexOf(SessionID);
		boolean isDriver = (index == Driver);
		return isDriver;
	}

	public static void setDriver(String SessionID){
		int index = activeSessions.indexOf(SessionID);
		Driver = index;
	}
}

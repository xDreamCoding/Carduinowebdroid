package de.carduinodroid.shared;

import java.util.ArrayList;
import de.carduinodroid.utilities.DBConnector;

public class activeSession {

	private static ArrayList<String> activeSessions;
	private static ArrayList<Integer> activeInt;
	static DBConnector db;
	
	public static void init(){
		activeSessions = new ArrayList<String>();
		activeInt = new ArrayList<Integer>();
		db = null;
		try {
			db = new DBConnector();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}
	
	public static void insertSession(String SessionID,String ipadress,String userid){
		int ID = -1;		
		ID = db.createSession(userid, ipadress);
		if (ID == -1){
			System.out.println("konnte Session nicht erstellen");
			return;
		}
		activeSessions.add(SessionID);
		activeInt.add(ID);
	}
	
	public static String[] getAllSessions(){
		String[] AllSessions = new String[activeSessions.size()];
		for(int i = 0; i < AllSessions.length; i++){
			AllSessions[i] = activeSessions.get(i);
		}
		return AllSessions;
	}
	
	public static void deleteSession(String SessionID){
		int index = activeSessions.indexOf(SessionID);
		if (index == -1){
			System.out.println("Session bereits gelöscht");
			return;
		}
		db.closeSession(getSessionInt(SessionID));
		activeSessions.remove(index);
		activeInt.remove(index);
	
		if (activeSessions.size() == 0){
			deleteAll();
		}
	}
	
	public static void deleteAll(){
		for (int i = 0; i < activeSessions.size(); i++){
			db.closeSession(getSessionInt(activeSessions.get(i)));
		}
		
		activeSessions.clear();
		activeInt.clear();		
	}

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
}

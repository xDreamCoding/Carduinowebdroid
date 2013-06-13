package de.carduinodroid.shared;

import java.util.ArrayList;
import java.net.Inet4Address;
import de.carduinodroid.utilities.DBConnector;

public class activeSession {

	private static ArrayList<String> activeSessions;
	private static ArrayList<Integer> activeInt;
	private static int LaufNummer = 0;
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
	
	public static void insertSession(String SessionID,String ipadress){
		Inet4Address ip4 = null;
		try{
			ip4 = (Inet4Address) Inet4Address.getByName(ipadress);
		}
		catch(Exception ie){
			System.out.println("kann IP nicht casten");
		}
		activeSessions.add(SessionID);
		activeInt.add(LaufNummer);

		if (!(ip4 == null)){
			db.createSession(db.getUserIdBySession(getSessionInt(SessionID)), ip4.toString());
		}
		else{
			db.createSession(db.getUserIdBySession(getSessionInt(SessionID)), "255.255.255.255");
		}
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
		activeSessions.clear();
		activeInt.clear();
		LaufNummer = 0;
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

package de.carduinodroid.shared;

import java.util.ArrayList;
import java.net.Inet4Address;
import de.carduinodroid.utilities.DBConnector;

public class activeSession {

	private static ArrayList<String> activeSessions;
	static DBConnector db;
	
	public static void init(){
		activeSessions = new ArrayList<String>();
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

		if (!(ip4 == null))db.createSession(db.getUserIdBySession(Integer.parseInt(SessionID)), ip4);
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
		activeSessions.remove(index);
	
		db.closeSession(Integer.parseInt(SessionID));
	}
	
	public static void deleteAll(){
		activeSessions.clear();
	}
}

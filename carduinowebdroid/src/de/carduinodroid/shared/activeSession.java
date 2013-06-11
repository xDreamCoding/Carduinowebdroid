package de.carduinodroid.shared;

import java.util.ArrayList;

public class activeSession {

	private static ArrayList<String> activeSessions;
	
	public static void init(){
		activeSessions = new ArrayList<String>();
	}
	
	public static void insertSession(String SessionID){
		activeSessions.add(SessionID);
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
		activeSessions.remove(index);
	}
	
	public static void deleteAll(){
		activeSessions.clear();
	}
}

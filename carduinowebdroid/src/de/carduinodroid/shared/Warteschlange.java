package de.carduinodroid.shared;

import java.util.LinkedList;

public class Warteschlange {

	private static LinkedList<String> Warteschlange = null;
	
	public static void initWarteschlange(){
		if (Warteschlange == null){
			Warteschlange = new LinkedList<String>();
		}
		else{
			
		}
	}
	
	public static LinkedList<String> getWarteschlange(){
		return Warteschlange;
	}
	
	public static String getNextUser(){
		return Warteschlange.removeFirst();
	}
	
	public static void insertUser(String SessionID){
		Warteschlange.add(SessionID);
	}
	
	public static int getLength(){
		return Warteschlange.size();
	}

	public static boolean isEmpty(){
		return Warteschlange.isEmpty();
	}
	
	public static void deleteTicket(String SessionID){
		int index = Warteschlange.indexOf(SessionID);
		Warteschlange.remove(index);
	}
	
	public static void InsertFirst(String SessionID){
		Warteschlange.addFirst(SessionID);
	}

	public static String[] getAllSessions(){
		String[] Liste = new  String [Warteschlange.size()];
		for (int i = 0;i < Warteschlange.size();i++){
			Liste[i] = Warteschlange.get(i);
		}
		return Liste;
	}
}

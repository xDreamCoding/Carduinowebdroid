package de.carduinodroid.shared;

import java.util.LinkedList;

public class Warteschlange {

	private LinkedList<String> Warteschlange;
	
	public Warteschlange(){
		Warteschlange = new LinkedList<String>();
	}
	
	public String getnextUser(){
		return Warteschlange.removeFirst();
	}
	
	public void insertUser(String SessionID){
		Warteschlange.add(SessionID);
	}
	
	public int getLength(){
		return Warteschlange.size();
	}

	public boolean isEmpty(){
		return Warteschlange.isEmpty();
	}
	
	public void deleteTicket(String SessionID){
		int index = Warteschlange.indexOf(SessionID);
		Warteschlange.remove(index);
	}
	
	public void InsertFirst(String SessionID){
		Warteschlange.addFirst(SessionID);
	}
}

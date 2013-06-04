package de.carduinodroid.shared;

import java.util.LinkedList;

public class Warteschlange {

	private LinkedList<Integer> Warteschlange;
	
	public Warteschlange(){
		Warteschlange = new LinkedList<Integer>();
	}
	
	public int getNextUser(){
		return Warteschlange.removeFirst();
	}
	
	public void insertUser(int SessionID){
		Warteschlange.add(SessionID);
	}
	
	public int getLength(){
		return Warteschlange.size();
	}

	public boolean isEmpty(){
		return Warteschlange.isEmpty();
	}
	
	public void deleteTicket(int SessionID){
		int index = Warteschlange.indexOf(SessionID);
		Warteschlange.remove(index);
	}
	
	public void InsertFirst(int SessionID){
		Warteschlange.addFirst(SessionID);
	}
}

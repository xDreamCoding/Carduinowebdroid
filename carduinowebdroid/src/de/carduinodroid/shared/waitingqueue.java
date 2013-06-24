package de.carduinodroid.shared;

import java.util.LinkedList;
import de.carduinodroid.utilities.DBConnector;

/**
 * \brief This Class is used to queue all users who want to get driver-rights
 * @author Alexander Rose
 *
 */

public class waitingqueue {

	private static LinkedList<String> Warteschlange = null;
	static DBConnector db;
	
	/** 
	 * \brief creates the internal waitingqueue and receives Connection to DB
	 * @param db2 DBConnector to our DB
	 */
	
	public static void initqueue(DBConnector db2){
		if (Warteschlange == null){
			Warteschlange = new LinkedList<String>();
			db = db2;
		}
		else{
			
		}
	}
	
	/** 
	 * \brief returns the waiting queue
	 * @return returns a LinkedList of Strings
	 */
	
	public static LinkedList<String> getqueue(){
		return Warteschlange;
	}
	
	/** 
	 * \brief returns the first User from the Queue and removes the User from the Queue
	 * @return returns the UserID from the first User as String
	 */
	
	public static String getNextUser(){
		return Warteschlange.removeFirst();
	}
	
	/** 
	 * \brief inserts a new User at the end of the waiting queue
	 * @param SessionID from the User
	 */
	
	public static void insertUser(String SessionID){
		if (Warteschlange.contains(SessionID)){
			System.out.println("Wurde bereits eingereiht");
			return;
		}
		Warteschlange.add(SessionID);
		System.out.println("User in Warteschlange eingereiht");
	}
	
	/** 
	 * @return returns the Length of the waiting queue
	 */
	
	public static int getLength(){
		return Warteschlange.size();
	}

	/** 
	 * @return Returns true if waiting queue is empty and false if its not empty
	 */
	
	public static boolean isEmpty(){
		return Warteschlange.isEmpty();
	}
	
	/** 
	 * \brief removes a User from the waiting queue
	 * @param SessionID of the User
	 */
	
	public static void deleteTicket(String SessionID){
		int index = Warteschlange.indexOf(SessionID);
		if (index == -1){
			System.out.println("Wurde bereits gelöscht");
			return;
		}
		Warteschlange.remove(index);
	}
	
	/** 
	 * \brief inserts a User at the first position of the waiting queue
	 * @param SessionID of the User
	 */
	
	public static void InsertFirst(String SessionID){
		Warteschlange.addFirst(SessionID);
	}
	
	/** 
	 * @return Returns the nicknames of all users in the waiting queue in the right order
	 */
	
	public static String[] getNickname(){
		String[] Nickname = new String[Warteschlange.size()];
		for(int i = 0;i < Nickname.length; i++){
			User user = db.getUserBySession(activeSession.getSessionInt(Warteschlange.get(i)));
			Nickname[i] = user.getNickname();
		}
		
		return Nickname;
	}
	
	/** 
	 * @return Returns the UserID from every User in the waiting queue in the right order
	 */
	
	public static String[] getAllSessions(){
		String[] Liste = new  String [Warteschlange.size()];
		for (int i = 0;i < Warteschlange.size();i++){
			Liste[i] = Warteschlange.get(i);
		}
		return Liste;
	}
}

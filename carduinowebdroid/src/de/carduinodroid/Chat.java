package de.carduinodroid;

import de.carduinodroid.shared.*;

public class Chat {

	public static void BroadcastChat(){
		String[] aktSessions = new String[activeSession.getAllSessions().length];
		aktSessions = activeSession.getAllSessions();
		String SenderSession = "test";
		//TODO empfange Nachricht
		//String msg = ;
		for (int i = 0; i < aktSessions.length;i++){
			if (aktSessions[i].equals(SenderSession) == true){
				continue;
			}
			//TODO sende Nachricht an User
			//TODO log chat
			//send(Session[i],msg);
		}
	}
}

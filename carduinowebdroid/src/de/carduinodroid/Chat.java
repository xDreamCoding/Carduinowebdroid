package de.carduinodroid;

import de.carduinodroid.shared.*;

/**
 * \brief This Class is used to forward the Chat-messages to all users
 * @author Alexander Rose
 *
 */

public class Chat {

	public static void BroadcastChat(){
		String[] aktSessions = new String[activeSession.getAllSessions().length];
		aktSessions = activeSession.getAllSessions();
		String SenderSession = "test";
		///TODO \todo empfange Nachricht
		//String msg = ;
		for (int i = 0; i < aktSessions.length;i++){
			if (aktSessions[i].equals(SenderSession) == true){
				continue;
			}
			///TODO \todo sende Nachricht an User
			///TODO \todo log chat
			//send(Session[i],msg);
		}
	}
}

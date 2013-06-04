package de.carduinodroid.shared;

import java.net.Inet4Address;
import java.sql.Timestamp;

public class Session {

	private String SessionId;
	private Inet4Address ip;
	private Timestamp LoginTime;
	
	public Session(String id,Inet4Address ipadress, Timestamp Login) {
		SessionId = id;
		ip = ipadress;
		LoginTime = Login;
	}

	public String getSessionId(){
		return SessionId;
	}
	
	public Inet4Address getIP(){
		return ip;
	}
	
	public Timestamp getLoginTme(){
		return LoginTime;
	}
}

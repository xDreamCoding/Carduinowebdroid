package de.carduinodroid.shared;

import java.net.Inet4Address;
import java.util.*;

public class Session {

	private String SessionId;
	private Inet4Address ip;
	private Date LoginTime;
	
	public Session(String id,Inet4Address ipadress,Date Login) {
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
	
	public Date getLoginTme(){
		return LoginTime;
	}
}

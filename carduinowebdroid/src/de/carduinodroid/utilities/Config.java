package de.carduinodroid.utilities;

import java.io.*;
import java.util.*;

public class Config {
	public class Options {
		public int fahrZeit;
		public String dbAddress;
		public String dbUser;
		public String dbPW;
		public boolean logChat;
		public boolean logChatToFile;
		public boolean logGPS;
		public boolean logGPSToFile;
	}
	
	private LogNG log;
	private Options options;
	private String filePath;
	
	public Config(LogNG logIN, String configPath) {
		this.log = logIN;
		filePath = configPath + "/config.properties";
		
		//logIN.writelogfile("filePath: " + filePath);
		
		options = new Options();
		
		File c = new File(configPath);
		File f = new File(filePath);
		if(!c.exists()) {
			logIN.writelogfile("creating folder");
			c.mkdir();
		}
		if(!f.exists())
			try {
				logIN.writelogfile("creating empty settings file");
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void readOptions() {
		try{
			Properties p = new Properties();			
			p.load(new FileInputStream(filePath));
			
			log.writelogfile("loading settings");
//			log.writelogfile("fahrZeit = " + p.getProperty("fahrZeit"));
//			log.writelogfile("dbAddress = " + p.getProperty("dbAddress"));
//			log.writelogfile("dbUser = " + p.getProperty("dbUser"));
//			log.writelogfile("dbPW = " + p.getProperty("dbPW"));
//			log.writelogfile("logChat = " + p.getProperty("logChat"));
//			log.writelogfile("logChatToFile = " + p.getProperty("logChatToFile"));
//			log.writelogfile("logGPS = " + p.getProperty("logGPS"));
//			log.writelogfile("logGPSToFile = " + p.getProperty("logGPSToFile"));
			
			Enumeration<Object>  keys = p.keys();
			while (keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				String value = (String)p.get(key);
				log.writelogfile(key + " = " + value);
			}

			
			options.fahrZeit = Integer.parseInt(p.getProperty("fahrZeit"));
			options.dbAddress = p.getProperty("dbAddress");
			options.dbUser = p.getProperty("dbUser");
			options.dbPW = p.getProperty("dbPW");
			options.logChat = Boolean.valueOf(p.getProperty("logChat"));
			options.logChatToFile = Boolean.valueOf(p.getProperty("logChatToFile"));
			options.logGPS = Boolean.valueOf(p.getProperty("logGPS"));
			options.logGPSToFile = Boolean.valueOf(p.getProperty("logGPSToFile"));
		}
		catch (Exception e) {
			  System.out.println(e);
		}
	}
	
	public void saveOptions() {
		try{
			log.writelogfile("saving settings");
			Properties p = new Properties();
			p.setProperty("fahrZeit", String.valueOf(options.fahrZeit));
			p.setProperty("dbAddress", options.dbAddress);
			p.setProperty("dbUser", options.dbUser);
			p.setProperty("dbPW", options.dbPW);
			p.setProperty("logChat", String.valueOf(options.logChat));
			p.setProperty("logChatToFile", String.valueOf(options.logChatToFile));
			p.setProperty("logGPS", String.valueOf(options.logGPS));
			p.setProperty("logGPSToFile", String.valueOf(options.logGPSToFile));
			p.store(new FileOutputStream(filePath), null);
		}
		catch (Exception e) {
			  System.out.println(e);
		}
	}
	
	public void setDefault() {    	
    	/*
    	 * DB läuft auf/im 
    	 * 	localhost 	-> true
    	 * 	FEM-Netz	-> false
    	 */
    	boolean localhost = false;	
    	options = new Options();
    	options.fahrZeit = 10;
    	if(localhost) {
    		options.dbAddress = "localhost:3306/carduinodroid";
    		options.dbUser = "root";
    		options.dbPW = "test";
    	} else {
    		options.dbAddress = "sehraf-pi:3306/carduinodroid";
    		options.dbUser = "test";
    		options.dbPW = "test";
    	}
		options.logChat = true;
		options.logChatToFile = false;
		options.logGPS = true;
		options.logGPSToFile = false;
    	saveOptions();
	}

	/**
	 * @return the options
	 */
	public Options getOptions() {
		return options;
	}
	
	public void setOptions(Options options) {
		this.options = options;
	}
	
	
}

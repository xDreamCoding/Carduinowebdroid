package de.carduinodroid.utilities;

import java.io.*;
import java.util.*;

import de.carduinodroid.desktop.Model.Log;


public class Config {
	public class Options {
		public int fahrZeit;
		public String dbAddress;
		public String dbUser;
		public String dbPW;
	}
	
	private Log log;
	private Options options;
	private InputStream in;
	private String filePath;
	
	public Config(Log log, String configPath) {
		this.log = log;
		filePath = configPath + "/config.properties";
		
		log.writelogfile("filePath: " + filePath);
		
		options = new Options();
		
		File c = new File(configPath);
		File f = new File(filePath);
		if(!c.exists()) {
			log.writelogfile("creating folder");
			c.mkdir();
		}
		if(!f.exists())
			try {
				log.writelogfile("creating empty settings file");
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
			log.writelogfile("fahrZeit = " + p.getProperty("fahrZeit"));
			log.writelogfile("dbAddress = " + p.getProperty("dbAddress"));
			log.writelogfile("dbUser = " + p.getProperty("dbUser"));
			log.writelogfile("dbPW = " + p.getProperty("dbPW"));
			
			options.fahrZeit = Integer.parseInt(p.getProperty("fahrZeit"));
			options.dbAddress = p.getProperty("dbAddress");
			options.dbUser = p.getProperty("dbUser");
			options.dbPW = p.getProperty("dbPW");
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
    	boolean localhost = true;	
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

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
	private String configPath = "config.ini";
	private Options options;
	
	public Config(Log log) {
		this.log = log;
		options = new Options();
		
		File f = new File(configPath);
		if(!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void readOptions() {
		try{
			Properties p = new Properties();
			p.load(new FileInputStream(configPath));
			
			System.out.println("fahrZeit = " + p.getProperty("fahrZeit"));
			System.out.println("dbAddress = " + p.getProperty("dbAddress"));
			System.out.println("dbUser = " + p.getProperty("dbUser"));
			System.out.println("dbPW = " + p.getProperty("dbPW"));
			
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
			Properties p = new Properties();
			p.setProperty("fahrZeit", String.valueOf(options.fahrZeit));
			p.setProperty("dbAddress", options.dbAddress);
			p.setProperty("dbUser", options.dbUser);
			p.setProperty("dbPW", options.dbPW);
			p.store(new FileOutputStream(configPath), null);
		}
		catch (Exception e) {
			  System.out.println(e);
		}
	}
	
	public void writeOptions(Options opt) {
		options = opt;
		saveOptions();
	}

	/**
	 * @return the options
	 */
	public Options getOptions() {
		return options;
	}
	
	
}

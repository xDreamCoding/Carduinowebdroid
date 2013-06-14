package de.carduinodroid.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import de.carduinodroid.desktop.Model.Log;
import de.carduinodroid.shared.GPS;
import de.carduinodroid.utilities.Config.Options;

public class LogNG {
	Log oldLog;
	Options options = null;
	DBConnector db;
	LinkedList<String> tmpLog;
	
	BufferedWriter writer;
	SimpleDateFormat dateformat;
	Date date;
	File file;
	File path;
	
	public LogNG() {
		oldLog = new Log();
		file = null;
		tmpLog = new LinkedList<String>();
	}
	
	/**
	 * log chat to DB or file depending on settings
	 * @param userID
	 * @param sessionID
	 * @param text
	 * @return true if successful
	 */
	public boolean logChat(String userID, int sessionID, String text) {
		if(options == null)
			throw new NullPointerException();
		
		if(!options.logChat)
			return true;
		
		if(options.logChatToFile)
		{
			// TODO - nicht unsere Aufgabe
			throw new UnsupportedOperationException("Not implemented yet");
		}
		else
			return db.logChat(userID, sessionID, text);
	}
	
	/**
	 * log GPS to DB or file depending on settings
	 * @param driveID
	 * @param longitude
	 * @param latitude
	 * @return true if successful
	 */
	public boolean logGPS(int driveID, String longitude, String latitude) {
		if(options == null)
			throw new NullPointerException();
		
		if(!options.logGPS)
			return true;
		
		if(options.logGPSToFile)
		{
			// TODO - nicht unsere Aufgabe
			throw new UnsupportedOperationException("Not implemented yet");
		}
		else
			return db.logGPS(driveID, longitude, latitude);
	}
	
	/**
	 * log GPS to DB or file depending on settings
	 * @param gps
	 * @return true if successful
	 */
	public boolean logGPS(GPS gps) {
		return (logGPS(gps.getDriveID(), gps.getLongitude(), gps.getLatitude()));
	}
	
	/**
	 * log enqueue event to DB or file depending on settings
	 * @param userID
	 * @param sessionID
	 * @return queueID
	 */
	public int logQueue(String userID, int sessionID) {
		if(options == null)
			throw new NullPointerException();
		
		int queueID = -1;
		if(!options.logQueue)
			return queueID;
		
		if(options.logQueueToFile)
		{
			// TODO - nicht unsere Aufgabe
			throw new UnsupportedOperationException("Not implemented yet");
		}
		else
			return db.logQueue(userID, sessionID);
	}

	/**
	 * downward compatible to old log
	 * @param string
	 */
	public void writelogfile(String string) {
		if(file == null) {
			tmpLog.addLast(string);
			writelogfile_second(string, false, true);
		}
		else
			writelogfile_second(string, false, false);
	}
	
	/** All Log entries will be imported by the same format.
	 *First you will get an information about the date and time
	 *and then a short explanation about the function.
	 *
	 *@param line Contains the string which will be include
	 */
	private void writelogfile_second(String line, boolean skipLiveLog, boolean skipFile){
		try {
			Date data = new Date();
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss" );
			String entry = df.format(data)+" "+line;
			if(!skipFile) {
				writer.write(entry,0,entry.length());
				writer.write(System.getProperty("line.separator"));
				writer.flush();
			}
			if(!skipLiveLog) write_Live_Log(entry);
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	/** 
	 *The log is written in a separate file but the user would
	 *like to see all information right in front of him. This
	 *method put all entries at the same time in our live log
	 *on the gui.
	 */
	private void write_Live_Log(String msg) {
		System.out.println(msg);
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(Options options) {		
		this.options = options;
		initFile();
		addTmpLog();
	}
	
	private void initFile() {
		Date date = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat( "yyyy_MM_dd_HH_mm_ss" );
		String logfile = "Log_"+dateformat.format(date)+".txt";
		
		File path = new File(options.filePath + "/logs");
		path.mkdirs();
		file = new File(path.getAbsolutePath(), logfile);

		try {
			file.createNewFile();
		} catch (IOException e) { e.printStackTrace(); }
		
		file.canWrite();
		file.canRead();
		
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void addTmpLog() {
		if(tmpLog == null) return;
		
		for(String s : tmpLog)
			writelogfile_second(s, true, false);
		
		tmpLog = null;
	}

	/**
	 * @return the oldLog
	 */
	public Log getOldLog() {
		return oldLog;
	}

	
	/**
	 * @param db the db to set
	 */
	public void setDB(DBConnector db) {
		this.db = db;
	}
		
}

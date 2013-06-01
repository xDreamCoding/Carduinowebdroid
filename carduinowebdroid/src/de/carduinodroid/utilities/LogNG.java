package de.carduinodroid.utilities;

import de.carduinodroid.desktop.Model.Log;
import de.carduinodroid.utilities.Config.Options;

public class LogNG {
	Log oldLog;
	Options options = null;
	DBConnector db;
	
	public LogNG() {
		oldLog = new Log();
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
		// TODO - soll für den moment reichen
		oldLog.writelogfile(string);
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(Options options) {
		this.options = options;
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

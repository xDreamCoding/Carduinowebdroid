package de.carduinodroid.utilities;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.carduinodroid.shared.*;
import de.carduinodroid.shared.User.Right;
import de.carduinodroid.utilities.Config.Options;

public class DBConnector {

	static Connection dbConnection = null;
	static LogNG log;
	static Options options;
	
	public DBConnector(LogNG logIN, Options opt) {
		log = logIN;
		options = opt;
		
		if(dbConnection == null)
			connect();
		
	}
	
	public DBConnector() throws Exception {
		if(log == null || options == null)
		throw new Exception("wrong contructor for first time instancing");
	}

	
	public void shutDown() {
		if(dbConnection == null)
			return;
		
		log.writelogfile("closing db connection!");
		
		try {
			dbConnection.commit();
			dbConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean connect() {
		try {
			if(dbConnection == null || dbConnection.isClosed()) {			
				Class.forName("org.mariadb.jdbc.Driver");
				dbConnection = DriverManager.getConnection("jdbc:mysql://" + options.dbAddress, options.dbUser, options.dbPW);
				log.writelogfile("DB Connection established.");
			} 
			else
				log.writelogfile("DB Connection already established.");
		}
		catch (Exception e) {
			log.writelogfile("DB Connection failed.");
			e.printStackTrace();
			return false;
		}
		return true;
	}	
		
	// --------------------- Hilfsfunktionen ---------------------
	private boolean executeUpdate(PreparedStatement stmt) {
		try {
			stmt.executeUpdate();
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
			
			try {
				if (stmt != null) { stmt.close(); }
			}
			catch (Exception e2) {
				log.writelogfile(e2.getMessage());
			}
			
			return false;
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				log.writelogfile(e.getMessage());
			}
		}
		return true;
	}
	
	private ResultSet executeQuery(PreparedStatement stmt) {
		ResultSet rset = null;
		try {	
			rset = stmt.executeQuery();
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
			
			try {
				if (stmt != null) { stmt.close(); }
			}
			catch (Exception e2) {
				log.writelogfile(e2.getMessage());				
			}
			return null;
		}
		// don't close the statement yet, you need it for the result set!
		return rset;
	}
	
	private void closeStatement(PreparedStatement stmt) {
		try {
			stmt.close();
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}		
	}
	
	// --------------------- API ---------------------
	// --- Chat ---
	/**
	 * save chat to DB
	 * @param userID
	 * @param sessionID
	 * @param text
	 * @return 
	 */
	protected boolean logChat(String userID, int sessionID, String text) {
		PreparedStatement stmt = null;
	
		if(text.length() > 256) {	// TODO: den Wert vllt zentral speichern? 
			log.writelogfile("logChat: text too long!");
			text = text.substring(0, 255);
		}
		
		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		
		try {
			stmt = dbConnection.prepareStatement("INSERT INTO chat (`time`, `userID`, `sessionID`, `text`) VALUES (?, ?, ?, ?)");
			stmt.setTimestamp(1, datetime);
			stmt.setString(2, userID);
			stmt.setInt(3, sessionID); 
			stmt.setString(4, text);
			
			executeUpdate(stmt);
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		return true;
	}
	
	// --- Drive ---	
	/**
	 * create a new drive
	 * @param userID of the driver
	 * @return driveID 
	 */
	public int startDrive(String userID) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int driveID = -1;
		
		// TODO: check if the userID exists

		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		
		// insert new session
		try {
			stmt = dbConnection.prepareStatement("INSERT INTO driver (`userID`, `startTime`) VALUES (?, ?);");
			stmt.setString(1, userID);
			stmt.setTimestamp(2, datetime);
			
			executeUpdate(stmt);
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}
		
		// lookup sessionID
		try {
			stmt = dbConnection.prepareStatement("SELECT driveID FROM driver WHERE `userID`=? AND `startTime`=?");
			stmt.setString(1, userID);
			stmt.setTimestamp(2, datetime);
			
			rset = executeQuery(stmt);
			
			if(!rset.isBeforeFirst()) {
				log.writelogfile("unable to create new session");
				return driveID; 	// -> return -1;
			} else {
				rset.next();
				driveID = rset.getInt("driveID");
			}			
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}
		
		closeStatement(stmt);
		
		return driveID;
	}

	
	/**
	 * add stop time to a given drive
	 * @param driveID
	 * @return true if successful
	 */
	public boolean stopDrive(int driveID) {
		PreparedStatement stmt = null;
		
		// TODO: check if the driveID exists

		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		
		try {
			stmt = dbConnection.prepareStatement("UPDATE driver SET `stopTime`=? WHERE `driveID`=?");
			stmt.setTimestamp(1, datetime);
			stmt.setInt(2, driveID);
			
			executeUpdate(stmt);
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}
		return true;
	}
	
	// --- GPS ---
	/**
	 * logs gps coordinates
	 * @param driveID
	 * @param longitude
	 * @param latitude
	 * @return true is successful
	 */
	protected boolean logGPS(int driveID, String latitude, String longitude) {
		PreparedStatement stmt = null;
		
		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		
		try {
			stmt = dbConnection.prepareStatement("INSERT INTO gps (`time`, `driveID`, `latitude`, `longitude`) VALUES (?, ?, ?, ?)");
			stmt.setTimestamp(1, datetime);
			stmt.setInt(2, driveID); 
			stmt.setString(3, latitude);
			stmt.setString(4, longitude);
			
			executeUpdate(stmt);
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		return true;
	}
	
	/**
	 * get all GPS records for one drive
	 * @param driveID
	 * @return ArrayList with GPS records
	 */
	public List<GPS> getGPSByDriveID(int driveID) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		GPS gps= null;
		List<GPS> list = new ArrayList<GPS>();
		
		Timestamp datetime;
		String latitude, longitude;
		
		try {
			stmt = dbConnection.prepareStatement("SELECT time, latitude, longitude FROM gps WHERE driveID=?");
			stmt.setInt(1, driveID);
			
			rset = executeQuery(stmt);
			
			if(!rset.isBeforeFirst()) {
				// kein gps infos gefunden
				return null;
			} else {
				rset.next();
				while(!rset.isAfterLast()){
					datetime = rset.getTimestamp("time");
					latitude = rset.getString("latitude");
					longitude = rset.getString("longitude");
					gps = new GPS(driveID, longitude, latitude, datetime);
							
					list.add(gps);
					gps = null;
					rset.next();
				}
			}			
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		closeStatement(stmt);
		
		return list;	
	}
	
	/**
	 * get all GPS records between a given time frame
	 * @param begin
	 * @param end
	 * @return ArrayList with GPS records
	 */
	public List<GPS> getGPSByTime(Timestamp begin, Timestamp end) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		GPS gps= null;
		List<GPS> list = new ArrayList<GPS>();
		
		int driveID;
		Timestamp datetime;
		String latitude, longitude;
		
		try {
			stmt = dbConnection.prepareStatement("SELECT time, driveID, latitude, longitude FROM gps WHERE time BETWEEN ? AND ?");
			stmt.setTimestamp(1, begin);
			stmt.setTimestamp(2, end);
			
			rset = executeQuery(stmt);
			
			if(!rset.isBeforeFirst()) {
				// kein gps infos gefunden
				return null;
			} else {
				rset.next();
				while(!rset.isAfterLast()){
					driveID = rset.getInt("driveID");
					datetime = rset.getTimestamp("time");
					latitude = rset.getString("latitude");
					longitude = rset.getString("longitude");
					gps = new GPS(driveID, longitude, latitude, datetime);
							
					list.add(gps);
					gps = null;
					rset.next();
				}
			}			
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		closeStatement(stmt);
		
		return list;	
	}
	
	// --- Session ---	
	/**
	 * create a new session
	 * @param userID
	 * @param ip address
	 * @return sessionID
	 */
	public int createSession(String userID, Inet4Address ip) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int sessionID = -1;
		
		// TODO: check if the userID exists

		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		
		// insert new session
		try {
			stmt = dbConnection.prepareStatement("INSERT INTO session (`userID`, `ipAddress`, `loginTime`) VALUES (?, ?, ?);");
			stmt.setString(1, userID);
			stmt.setString(2, ip.getHostAddress());
			stmt.setTimestamp(3, datetime);
			
			executeUpdate(stmt);
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}
		
		// lookup sessionID
		try {
			stmt = dbConnection.prepareStatement("SELECT sessionID FROM session WHERE `userID`=? AND `ipAddress`=? AND `loginTime`=?");
			stmt.setString(1, userID);
			stmt.setString(2, ip.getHostAddress());
			stmt.setTimestamp(3, datetime);
			
			rset = executeQuery(stmt);
			
			if(!rset.isBeforeFirst()) {
				log.writelogfile("unable to create new session");
				return sessionID; 	// -> return -1;
			} else {
				rset.next();
				sessionID = rset.getInt("sessionID");
			}			
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}
		
		closeStatement(stmt);
		
		return sessionID;
	}

	
	/**
	 * add logout time to a given session
	 * @param sessionID
	 * @return true if successful
	 */
	public boolean closeSession(int sessionID) {
		PreparedStatement stmt = null;
		
		// TODO: check if the sessionID exists

		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		
		try {
			stmt = dbConnection.prepareStatement("UPDATE session SET `logoutTime`=? WHERE `sessionID`=?");
			stmt.setTimestamp(1, datetime);
			stmt.setInt(2, sessionID);
			
			executeUpdate(stmt);
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}
		return true;
	}
	
	
	// --- User ---
	/** 
	 * try to login with a given userID and password
	 * @param userID (login)
	 * @param password
	 * @return returns the user as a User object or null if the login was invalid 
	 */
	public User loginUser(String userID, String pw) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		User user = null;
		try {
			stmt = dbConnection.prepareStatement("SELECT nickname, rightFlag FROM user WHERE userID = ? AND password = ?");
			stmt.setString(1, userID);
			stmt.setString(2, pw); // TODO: pw hashen
			
			rset = executeQuery(stmt);
			
			if(!rset.isBeforeFirst()) {
				//throw new IllegalArgumentException("Login invalid.");
				// TODO: log invalid login attempts?
				//log.writelogfile("invalid login attempt! user: " + userID + " pw: " + pw);
			} else {
				rset.next();
				String nickname = rset.getString("nickname");
				if(nickname == null) nickname = userID;
				byte right = rset.getByte("rightFlag");
				user = new User(userID, nickname, Right.values()[right]);
			}			
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		closeStatement(stmt);
		
		return user;
	}

	/**
	 * change the nicknam 
	 * @param userID (login)
	 * @param new nickname
	 * @return true if successful
	 */
	public boolean changeNickname(String userID, String newNick) {
		PreparedStatement stmt = null;
		
		try {
			stmt = dbConnection.prepareStatement("UPDATE user SET `nickname`=? WHERE `userID`=?");
			stmt.setString(1, newNick);
			stmt.setString(2, userID);			
			
			executeUpdate(stmt);
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		return true;
	}
	
	/** 
	 * create a new user
	 * @param userID (login)
	 * @param nickname
	 * @param password
	 * @param right
	 * @return true if successful
	 */
	public boolean createUser(String userID, String nick, String pw, Right r) {
		PreparedStatement stmt = null;
		
		try {
			stmt = dbConnection.prepareStatement("INSERT INTO user (`userID`, `nickname`, `password`, `rightFlag`) VALUES (?, ?, ?, ?)");
			stmt.setString(1, userID);
			stmt.setString(2, nick);
			stmt.setString(3, pw); 	//TODO: pw hashen
			stmt.setByte(4, (byte)r.ordinal());
			
			executeUpdate(stmt);
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		return true;
	}
	
	/**
	 * delete a user
	 * @param userID to delete
	 * @return true if successful
	 */
	private boolean deleteUser(String userID) {
		PreparedStatement stmt = null;
		
		try {
			stmt = dbConnection.prepareStatement("DELETE FROM user WHERE `userID`=?");
			stmt.setString(1, userID);
			
			executeUpdate(stmt);
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}
		
		return true;
	}

	/**
	 * get all user 
	 * @return returns all users in the database in an ArrayList
	 */
	public List<User> getAllUser() {
		// TODO: besondere filter?
		
		PreparedStatement stmt = null;
		ResultSet rset = null;
		User user = null;
		List<User> list = new ArrayList<User>();
		
		String userID, nickname;
		byte right;
		
		try {
			stmt = dbConnection.prepareStatement("SELECT userID, nickname, rightFlag FROM user");
			
			rset = executeQuery(stmt);
			
			if(!rset.isBeforeFirst()) {
				// kein user?
				return null;
			} else {
				rset.next();
				while(!rset.isAfterLast()){
					userID = rset.getString("userID");
					nickname = rset.getString("nickname");
					if(nickname == null) nickname = userID;
					right = rset.getByte("rightFlag");
					user = new User(userID, nickname, Right.values()[right]);
					
					list.add(user);
					user = null;
					rset.next();
				}
			}			
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		closeStatement(stmt);
		
		return list;	
	}
	
	/**
	 * get the number of all user in the database
	 * @return number of users
	 */
	public int getAllUserCount() {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int returnValue = 0;

		try {
			stmt = dbConnection.prepareStatement("SELECT Count(userID) from user");
			
			rset = executeQuery(stmt);
			
			if(!rset.isBeforeFirst()) {
				// kein user?
				return 0;
			} else {
				rset.next();
				returnValue = rset.getInt(1);
			}			
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		closeStatement(stmt);
		
		return returnValue;	
	}
	
	/**
	 * look up a userID by a given sessionID
	 * @param sessionID 
	 * @return userID or null if no session was found
	 */
	public String getUserIdBySession(int sessionID) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String userID = null;

		try {
			stmt = dbConnection.prepareStatement("SELECT userID FROM session WHERE sessionID=?");
			stmt.setInt(1, sessionID);
			
			rset = executeQuery(stmt);
			
			if(!rset.isBeforeFirst()) {
				// kein eintrag?
				return userID;
			} else {
				rset.next();
				userID = rset.getString("userID");
			}			
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		closeStatement(stmt);
		
		return userID;	
	}
	
	/**
	 * get a user object based on an given session ID
	 * @param sessionID
	 * @return user or null
	 */
	public User getUserBySession(int sessionID) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		User user = null;
		try {
			stmt = dbConnection.prepareStatement("SELECT userID, nickname, rightFlag FROM user WHERE userID = (SELECT userID FROM session WHERE sessionID=?)");
			stmt.setInt(1, sessionID);
			
			rset = executeQuery(stmt);
			
			if(!rset.isBeforeFirst()) {
				// no user found
			} else {
				rset.next();
				String userID = rset.getString("userID");
				String nickname = rset.getString("nickname");
				if(nickname == null) nickname = userID;
				byte right = rset.getByte("rightFlag");
				user = new User(userID, nickname, Right.values()[right]);
			}			
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}

		closeStatement(stmt);
		
		return user;
	}
	
	// --- Queue ---
	/**
	 * log a user who enqueued himself
	 * @param userID
	 * @param sessionID
	 * @return queueID
	 */
	protected int logQueue(String userID, int sessionID) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int queueID = -1;
		
		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		
		// insert new session
		try {
			stmt = dbConnection.prepareStatement("INSERT INTO waitingQueue (`userID`, `sessionID`, `time`) VALUES (?, ?, ?);");
			stmt.setString(1, userID);
			stmt.setInt(2, sessionID);
			stmt.setTimestamp(3, datetime);
			
			executeUpdate(stmt);
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}
		
		// lookup sessionID
		try {
			stmt = dbConnection.prepareStatement("SELECT queueID FROM waitingQueue WHERE `userID`=? AND `sessionID`=? AND `time`=?");
			stmt.setString(1, userID);
			stmt.setInt(2, sessionID);
			stmt.setObject(3, datetime);
			
			rset = executeQuery(stmt);
			
			if(!rset.isBeforeFirst()) {
				log.writelogfile("unable to create new queue entry");
				return queueID; 	// -> return -1;
			} else {
				rset.next();
				queueID = rset.getInt("queueID");
			}			
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
		}
		
		closeStatement(stmt);
		
		return queueID;
	}

	
	// --------------------- self tests ---------------------
	
	public void dbTest() {
		String userID = "test";
		
		System.out.println("starting DB test!");
		
		/**
		 * testet der Verbindung
		 * Erwartung: Verbindung ist ok
		 */
		System.out.print("testing connection ... ");
		try {
			if(dbConnection.isValid(5)) {
				System.out.print("OK\n");
			} else {
				System.out.print("BAD - connection is NOT valid! exiting ...\n");
				return;
			}
		} catch (SQLException e) {
			System.out.println("error: " + e.getMessage());
			return;
		}
		System.out.println("");
		
		dbUserTest();
		int sessionID = dbSessionTest(userID);
		if(sessionID < 0)
		{
			System.out.println("session test has failed! exiting...");
			return;
		}
		dbChatTest(userID, sessionID);
		
		int driveID = dbDriveTest(userID);
		if(driveID < 0)
		{
			System.out.println("drive test has failed! exiting...");
			return;
		}
		dbGPSTest(driveID, "51°03'09.5\"", "001°07'54.9\"");
		
		dbQueueTest(userID, sessionID);
		
		System.out.println("test done!");
	}

	private void dbChatTest(String userID, int sessionID) {
		String txt = String.valueOf((int)(Math.random() * Integer.MAX_VALUE));
		
		System.out.println("starting chat test!");
		
		/**
		 * Chat speichern
		 * Erwartung: geht
		 */
		System.out.print("saving random chat text ... ");
		if(logChat(userID, sessionID, txt)) 
			System.out.print("OK\n");
		else {
			System.out.print("BAD\n");
			return;
		}
		
		System.out.println("chat test done!");
		System.out.println("");
	}
		
	private int dbDriveTest(String userID) {
		int driveID = -1;
		
		System.out.println("starting drive test!");		
		
		/**
		 * Fahrt beginnen
		 * Erwartung: sessionID > -1
		 */
		System.out.print("creating new drive ... ");
		driveID = startDrive(userID);
		System.out.print("driveID: " + driveID + " ");
		if(driveID > -1) 
			System.out.print("OK\n");
		else {
			System.out.print("BAD\n");
			return driveID;
		}
		
		/**
		 * Session schließen
		 * Erwartung: geht
		 */
		System.out.print("finishing drive ... ");
		if(stopDrive(driveID)) 
			System.out.print("OK\n");
		else
			System.out.print("BAD\n");
		
		System.out.println("drive test done!");
		System.out.println("");
		return driveID;
	}

	private void dbGPSTest(int driveID, String longitude, String latitude) {
		System.out.println("starting GPS test!");		
		
		/**
		 * Koordinaten speichern
		 * Erwartung: geht
		 */
		System.out.print("saving GPS ... ");
		if(logGPS(driveID, longitude, latitude)) 
			System.out.print("OK\n");
		else {
			System.out.print("BAD\n");
			return;
		}
		
		GPS tmpGPS;
		List<GPS> list;
		
		/**
		 * get GPS by driveID
		 */
		System.out.print("getting GPS record for driveID " + driveID + " ... ");
		list = getGPSByDriveID(driveID);
		if(list.isEmpty()) {
			System.out.print("BAD - list is empty\n");
		} else {
			System.out.print("OK\n");
			System.out.println("time\t\tlat\t\tlong");
			for(Iterator<GPS> it = list.iterator(); it.hasNext(); ) {
				tmpGPS = it.next();
				System.out.println(tmpGPS.getDateTimeFormated() + "\t" + tmpGPS.getLatitude() + "\t" + tmpGPS.getLongitude());
			}
		}
		
		/**
		 * get GPS by time frame
		 */
		System.out.print("getting GPS record by time frame ... ");
		Timestamp begin = new Timestamp(System.currentTimeMillis() - 1000 * 60 * 5), end = new Timestamp(System.currentTimeMillis() + 1000 * 60 * 5);
		list = getGPSByTime(begin, end);
		if(list.isEmpty()) {
			System.out.print("BAD - list is empty\n");
		} else {
			System.out.print("OK\n");
			System.out.println("driveID\tlat\t\tlong");
			for(Iterator<GPS> it = list.iterator(); it.hasNext(); ) {
				tmpGPS = it.next();
				System.out.println(tmpGPS.getDriveID() + "\t" + tmpGPS.getLatitude() + "\t" + tmpGPS.getLongitude());
			}
		}
		
		System.out.println("GPS test done!");
		System.out.println("");
	}
	
	private int dbQueueTest(String userID, int sessionID) {
		int queueID = -1;
		System.out.println("starting queue test!");		
		
		/**
		 * Eintrag speichern 
		 * Erwartung: geht
		 */
		System.out.print("saving enqueue event ... ");
		queueID = logQueue(userID, sessionID);
		if(queueID > -1) 
			System.out.print("OK\n");
		else {
			System.out.print("BAD\n");
			return queueID;
		}
		
		System.out.println("queue test done!");
		System.out.println("");
		return queueID;
	}

	private int dbSessionTest(String userID) {
		int sessionID = -1;
		Inet4Address addr;
		
		
		try {
			addr = (Inet4Address) Inet4Address.getByName("localhost");
		} catch (UnknownHostException e) {
			System.out.println("localhost ist nicht bekannt :S");
			return sessionID;
		}
		
		System.out.println("starting session test!");		
		
		/**
		 * Session erstellen
		 * Erwartung: sessionID > -1
		 */
		System.out.print("creating new session ... ");
		sessionID = createSession(userID, addr);
		System.out.print("sessionID: " + sessionID + " ");
		if(sessionID > -1) 
			System.out.print("OK\n");
		else {
			System.out.print("BAD\n");
			return sessionID;
		}
		
		/**
		 * Session schließen
		 * Erwartung: geht
		 */
		System.out.print("closing new session ... ");
		if(closeSession(sessionID)) 
			System.out.print("OK\n");
		else
			System.out.print("BAD\n");
		
		/**
		 * UserID von Session nachgucken
		 * Erwartung findet "userID"
		 */
		System.out.print("looking up userID by sessionID ... ");
		String uID = getUserIdBySession(sessionID);
		if(uID.equals(userID)) 
			System.out.print("OK\n");
		else
			System.out.print("BAD - got " + uID + " instead of " + userID + "\n");
		
		System.out.println("session test done!");
		System.out.println("");
		return sessionID;
	}
	
	private void dbUserTest() {
		String pw = String.valueOf((int)(Math.random() * 100));
		String user = "u" + pw;
		String nick = "n" + pw;
		User u;
		
		System.out.println("starting user test!");
		
		/**
		 * Versucht sich mit einem ungültigen User einzuloggen 
		 * Erwartung: schlägt fehl
		 */
		System.out.print("trying to login in as (invalid) user " + user + "... ");
		if(loginUser(user, pw) == null) {
			System.out.print("OK\n");
		} else {
			System.out.print("BAD - user seems to exist ... continuing\n");
		}
		
		/**
		 * Legt neuen Test-User an (ohne nick)
		 * Erwartung: geht
		 */
		System.out.print("creating user " + user + "... ");
		if(createUser(user, null, pw, Right.GUEST)) {
			System.out.print("OK\n");
		} else {
			System.out.print("BAD - error while creating user " + user + ". exiting ...\n");
			return;
		}
		
		/**
		 * Versucht sich als der neue User einzuloggen 
		 * Erwartung: geht; Nickname == user
		 */
		System.out.print("login in as user " + user + "... ");
		u = loginUser(user, pw);
		if(u != null) {
			System.out.print("Success\n");
			if(u.getNickname().equals(user)) {
				System.out.print("--> nickname (" + u.getNickname() + ") == user (" + user + ") - OK\n");
				System.out.println("--> isGuest() = " + (u.isGuest() ? "true - OK" : "false - BAD"));
			}
			else
				System.out.print("--> nickname (" + u.getNickname() + ") != user (" + user + ") - BAD! continuing ...\n");
		} else {
			System.out.print("BAD - error! exiting ...\n");
			return;
		}
		
		/**
		 * Ändern des Nicknames
		 * Erwartung: geht
		 */
		System.out.print("changing nickname to " + nick + "... ");
		if(changeNickname(user, nick)) {
			System.out.print("OK\n");
		} else {
			System.out.print("BAD - error while changing nickname! exiting ...\n");
			return;
		}

		/**
		 * Versucht sich nochmal als der neue User einzuloggen 
		 * Erwartung: geht; Nickname == nick
		 */
		System.out.print("login in as user " + user + "... ");
		u = null;
		u = loginUser(user, pw);
		if(u != null) {
			System.out.print("-> Success\n");
			if(u.getNickname().equals(nick))
				System.out.print("--> nickname (" + u.getNickname() + ") == nick (" + nick + ") - OK\n");
			else
				System.out.print("--> nickname (" + u.getNickname() + ") != nick (" + nick + ") - BAD! continuing ...\n");
		} else {
			System.out.print("error! exiting ...");
			return;
		}
		
		/**
		 * zähle user in DB
		 */
		System.out.print("counting all users ... ");
		int userCount = getAllUserCount();
		if(userCount > 0)
			System.out.print("OK - counting " + userCount + " users\n");
		else
			System.out.print("BAD - no users found\n");
		
		/**
		 * Holt alle User aus der DB
		 * Erwartung: Liste, mit mind einem User
		 */
		System.out.print("fetching all users ... ");
		List<User> list = new ArrayList<User>();
		User tmpU;
		list = getAllUser();
		if(list.isEmpty()) {
			System.out.print("BAD - list is empty\n");
		} else {
			System.out.print("OK\n");
			System.out.println("userID\tnick\tUser");
			for(Iterator<User> it = list.iterator(); it.hasNext(); ) {
				tmpU = it.next();
				System.out.println(tmpU.getUserID() + "\t" + tmpU.getNickname() + "\t" + (tmpU.isUser() ? "true" : "false"));
			}
		}
		
		/**
		 * Löscht den Test-User
		 * Erwartung: geht
		 */
		System.out.print("deleting user " + user + "... ");
		if(deleteUser(user)) {
			System.out.print("OK\n");
		} else {
			System.out.print("BAD - error while deleting user " + user + ". exiting ...\n");
			return;
		}
		
		/**
		 * Versucht sich mit einem ungültigen User einzuloggen 
		 * Erwartung: schlägt fehl
		 */
		System.out.print("trying to login in as (invalid) user " + user + "... ");
		if(loginUser(user, pw) == null) {
			System.out.print("OK\n");
		} else {
			System.out.print("BAD - user seems to exist ... continuing\n");
		}
		
		System.out.println("user test done!");
		System.out.println("");
	}
}

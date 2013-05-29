package de.carduinodroid.utilities;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.carduinodroid.desktop.Model.Log;
import de.carduinodroid.shared.User;
import de.carduinodroid.shared.User.Right;
import de.carduinodroid.utilities.Config.Options;

public class DBConnector {

	static Connection dbConnection = null;
	Log log;
	Options options;
	
	public DBConnector(Log l, Options opt) {
		log = l;
		options = opt;
		
		if(dbConnection == null)
			connect();
		
	}
	
	public void shutDown() {
		if(dbConnection == null)
			return;
		
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
		
	// --------------------- API ---------------------
	// --- User ---
	/**
	 * TODO: pw hashen
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
			stmt.setString(2, pw);
			
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
		
		try {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return user;
	}

	/**
	 * 
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
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * TODO: pw hashen
	 * 
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
			stmt.setString(3, pw);
			stmt.setByte(4, (byte)r.ordinal());
			
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
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param userID to delete
	 * @return true if successful
	 */
	private boolean deleteUser(String userID) {
		PreparedStatement stmt = null;
		
		try {
			stmt = dbConnection.prepareStatement("DELETE FROM user WHERE `userID`=?");
			stmt.setString(1, userID);
			
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
				e.printStackTrace();
			}
		}
		return true;
	}

	// --- Session ---
	
	/**
	 * 
	 * @param userID
	 * @param ip address
	 * @return sessionID
	 */
	public int createSession(String userID, Inet4Address ip) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int sessionID = -1;
		
		// TODO: check if the userID exists

		Object datetime = new java.sql.Timestamp(System.currentTimeMillis());
		
		// insert new session
		try {
			stmt = dbConnection.prepareStatement("INSERT INTO session (`userID`, `ipAddress`, `loginTime`) VALUES (?, ?, ?);");// +
					//"SELECT sessionID FROM session WHERE `userID`=? AND `ipAddress`=? AND `loginTime`=?");
			stmt.setString(1, userID);
			stmt.setString(2, ip.getHostAddress());
			stmt.setObject(3, datetime);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
			
			try {
				if (stmt != null) { stmt.close(); }
			}
			catch (Exception e2) {
				log.writelogfile(e2.getMessage());
			}
			
			return -1;
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// lockup sessionID
		try {
			stmt = dbConnection.prepareStatement("SELECT sessionID FROM session WHERE `userID`=? AND `ipAddress`=? AND `loginTime`=?");
			stmt.setString(1, userID);
			stmt.setString(2, ip.getHostAddress());
			stmt.setObject(3, datetime);
			
			rset = stmt.executeQuery();
		} catch (SQLException e) {
			log.writelogfile(e.getMessage());
			
			try {
				if (stmt != null) { stmt.close(); }
			}
			catch (Exception e2) {
				log.writelogfile(e2.getMessage());
			}
			
			return -1;
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			if(!rset.isBeforeFirst()) {
				log.writelogfile("unable to create new session");
				return -1;
			} else {
				rset.next();
				sessionID = rset.getInt("sessionID");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		
		return sessionID;
	}
	
	/**
	 * 
	 * @param sessionID
	 * @return true if successful
	 */
	public boolean closeSession(int sessionID) {
		PreparedStatement stmt = null;
		
		// TODO: check if the sessionID exists

		Object datetime = new java.sql.Timestamp(System.currentTimeMillis());
		
		try {
			stmt = dbConnection.prepareStatement("UPDATE session SET `logoutTime`=? WHERE `sessionID`=?");
			stmt.setObject(1, datetime);
			stmt.setInt(2, sessionID);
			
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
				e.printStackTrace();
			}
		}
		return true;
	}
	
	
	// --------------------- self tests ---------------------
	
	public void dbTest() {
		System.out.print("starting DB test! \n");
		
		System.out.print("starting user test! \n");
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
			System.out.print("error: " + e.getMessage()+"\n");
			return;
		}
		
		dbUserTest();
		dbSessionTest();
		
		System.out.print("test done!\n");
	}
	
	private void dbSessionTest() {
		int sessionID = -1;
		Inet4Address addr;
		
		
		try {
			addr = (Inet4Address) Inet4Address.getByName("localhost");
		} catch (UnknownHostException e) {
			System.out.print("localhost ist nicht bekannt :S");
			return;
		}
		
		System.out.print("starting user test! \n");		
		
		/**
		 * Session erstellen
		 * Erwartung: sessionID > -1
		 */
		System.out.print("creating new session ... ");
		sessionID = createSession("test", addr);
		System.out.print("sessionID: " + sessionID + " ");
		if(sessionID > -1) 
			System.out.print("OK\n");
		else {
			System.out.print("BAD\n");
			return;
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
		
		System.out.print("(dummy session aus db löschen)\n");
		
		System.out.print("session test done!\n");
	}
	
	private void dbUserTest() {
		String pw = String.valueOf((int)(Math.random() * 100));
		String user = "u" + pw;
		String nick = "n" + pw;
		User u;
		
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
			if(u.getNickname().equals(user))
				System.out.print("--> nickname (" + u.getNickname() + ") == user (" + user + ") - OK\n");
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
		
		System.out.print("user test done!\n");
	}
}

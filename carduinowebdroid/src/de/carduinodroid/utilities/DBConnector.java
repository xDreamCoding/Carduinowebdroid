package de.carduinodroid.utilities;

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
			// TODO Auto-generated catch block
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
	
	/**
	 * TODO: muss das public sein?
	 * @return the dbConnection 
	 */
	public Connection getDbConnection() {
		if(dbConnection == null){
			if(connect()) return dbConnection;
			else  return null;			
		}
		return dbConnection;
	}
	
	// API
	
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
			// TODO Auto-generated catch block
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
				//log.writelogfile("invalid login attempt! user: " + userID + " pw: " + pw);
			} else {
				rset.next();
				String nickname = rset.getString("nickname");
				if(nickname == null) nickname = userID;
				byte right = rset.getByte("rightFlag");
				user = new User(userID, nickname, Right.toRight(right));
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
	 * @return true if success
	 */
	public boolean changeNickname(String userID, String newNick) {
		PreparedStatement stmt = null;
		
		try {
			stmt = dbConnection.prepareStatement("UPDATE user SET `nickname`=(?) WHERE `userID`=(?)");
			stmt.setString(1, newNick);
			stmt.setString(2, userID);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * TODO: pw hashen
	 * @param userID (login)
	 * @param nickname
	 * @param password
	 * @param right
	 * @return true if success
	 */
	public boolean createUser(String userID, String nick, String pw, Right r) {
		PreparedStatement stmt = null;
		
		try {
			stmt = dbConnection.prepareStatement("INSERT INTO user (`userID`, `nickname`, `password`, `rightFlag`) VALUES ((?), (?), (?), (?))");
			stmt.setString(1, userID);
			stmt.setString(2, nick);
			stmt.setString(3, pw);
			stmt.setByte(4, Right.toByte(r));
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param userID to delete
	 * @return true if success
	 */
	private boolean deleteUser(String userID) {		
		PreparedStatement stmt = null;
		
		try {
			stmt = dbConnection.prepareStatement("DELETE FROM user WHERE `userID`=(?);");
			stmt.setString(1, userID);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	
	// test programm
	
	public void dbTest() {
		String pw = String.valueOf((int)(Math.random() * 100));
		String user = "u" + pw;
		String nick = "n" + pw;
		User u;
		
		System.out.print("starting DB test! \n");
		
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
		
		/**
		 * Versucht sich mit einem ungültigen User einzuloggen 
		 * Erwartung: schlägt fehl
		 */
		System.out.print("login in as user " + user + "... ");
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
		System.out.print("login in as user " + user + "... ");
		if(loginUser(user, pw) == null) {
			System.out.print("OK\n");
		} else {
			System.out.print("BAD - user seems to exist ... continuing\n");
		}
		
		System.out.print("Test done!\n");
	}
}

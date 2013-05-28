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
	
//	private Statement dbQuery(String statement)
//	{
//		try {
//			Statement stmt = dbConnection.createStatement();
//			stmt.execute(statement);
//			//ResultSet rset = stmt.getResultSet();
//			//stmt.close();
//			return stmt;			
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw new IllegalArgumentException("Database Error.");
//		}
//	}
	
	/**
	 * @return the dbConnection
	 * TODO: muss das public sein?
	 */
	public Connection getDbConnection() {
		if(dbConnection == null){
			if(connect()) return dbConnection;
			else  return null;			
		}
		return dbConnection;
	}
	
	public User login(String userID, String pw) {
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
				throw new IllegalArgumentException("Login invalid.");
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
}

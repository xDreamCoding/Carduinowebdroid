package de.carduinodroid.shared;

public class User {
	public enum Right {
		GUEST, USER, ADMIN;
		
		public static byte toByte(Right r) {
			switch(r){
			case GUEST:
				return 0;
			case USER:
				return 1;
			case ADMIN:
				return 2;
			}
			return 0;
		}
		
		public static Right toRight(byte b) {
			switch(b) {
			case 0:
				return GUEST;
			case 1:
				return USER;
			case 2:
				return ADMIN;
			}
			return GUEST;
		}
	}	
	
	public String userID; 		// login name 
	public String nickname;	// anzeige name
	private Right right = Right.GUEST;
	
	public User() {	}
	
	public User (String id, Right r) {
		userID = id;
		right = r;
	}

	public User (String id, String nick, Right r) {
		userID = id;
		nickname = nick;
		right = r;
	}

	
	public boolean isAdmin() { return right == Right.ADMIN; }
	public boolean isUser() { return right == Right.USER; }
	public boolean isGUEST() { return right == Right.GUEST; }
//	public void setRight(Right r) { right = r; }
	
	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * @return the userNickname
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * @param userNickname the userNickname to set
	 */
	public void setNickname(String userNickname) {
		this.nickname = userNickname;
	}	

}

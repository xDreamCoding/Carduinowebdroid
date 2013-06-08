package de.carduinodroid.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.carduinodroid.shared.User;

public class UserTag extends TagSupport{
	private int UserNumber;
	
	public void setNum(int i) {
		UserNumber = i;
	}
	
	public String UserName() throws JspException{
		String Name;
		try {
			List<User> UserList = new ArrayList<User>();
			UserList = getAllUser(); //TODO: Not working. Don't know why.
			
			Name = UserList.get(UserNumber).getNickname();
			
		} catch(IOException ioe) {
			throw new JspException("Error: " + ioe.getMessage());
		}
		return Name;
		
		
	}

}

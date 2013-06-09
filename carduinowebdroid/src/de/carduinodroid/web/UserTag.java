package de.carduinodroid.web;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspWriter;
import de.carduinodroid.utilities.DBConnector;

import de.carduinodroid.shared.User;

public class UserTag extends TagSupport{
	private int UserNumber;
	
	public void setNum(int i) {
		UserNumber = i;
	}
	
	public int doStartTag() throws JspException{
		
		String Name;
		DBConnector db = (DBConnector)pageContext.getServletContext().getAttribute("database");
		JspWriter out = pageContext.getOut();
		
		List<User> UserList = new ArrayList<User>();
		UserList = db.getAllUser();
		
		Name = UserList.get(UserNumber).getNickname();
		
		
		try {
			out.print(Name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return EVAL_PAGE;
		
		
	}

	public int doEndTag() throws JspException{
		return EVAL_PAGE;
	}
	
}

package de.carduinodroid.web;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspWriter;
import de.carduinodroid.utilities.DBConnector;

import de.carduinodroid.shared.User;

/**
 * Tag-Handler for all User-Tags in JSP files.
 * @author Christoph Braun
 *
 */
public class UserTag extends TagSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int userNr;
	private int param;
//	private int totalNr;
	
	/*
	 * 
	 */
	public void setNum(int i) {
		userNr = i;
	}
	
	public void setPar(int i) {
		param = i;
	}
	
	public int doStartTag() throws JspException{
		
		String Name;
		boolean isAdmin;
		DBConnector db = (DBConnector)pageContext.getServletContext().getAttribute("database");
		JspWriter out = pageContext.getOut();
		List<User> UserList = new ArrayList<User>();
		UserList = db.getAllUser();
		
		/*
		 * @param param =1: Get Nickname from User
		 * @param param =2: Check if User is Admin
		 */
		switch(param) {
		
		case 1:	
			try {
				Name = UserList.get(userNr).getNickname();			
				out.print(Name);
			} catch (IOException e) {
				throw new JspException("Error: " + e.getMessage());
			}
			break;
			
		case 2:
			try {
				isAdmin = UserList.get(userNr).isAdmin();
				out.print(isAdmin);
			} catch (IOException e) {
				throw new JspException("Error: " + e.getMessage());
			}
			break;
		
//		case 3:
//			try {
//				totalNr = UserList.size();
//				out.print(totalNr);
//			} catch (IOException e) {
//				throw new JspException("Error: " + e.getMessage());
//			}
//			break;
			
		default:
			throw new JspException("Error: invalid parameter in UserTag");
		}
		return EVAL_PAGE;
		
		
	}

	public int doEndTag() throws JspException{
		return EVAL_PAGE;
	}
	
}

package de.carduinodroid.web;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import de.carduinodroid.shared.activeSession;
import de.carduinodroid.shared.waitingqueue;

public class QueueTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	private int parameter;
	
	public void setPar(int i) {
		parameter = i ;
	}
	
	
	public int doStartTag() throws JspException{
		
		JspWriter out = pageContext.getOut();
		String[] ListQ = new  String [waitingqueue.getLength()];
		ListQ = waitingqueue.getNickname();
		String driverName;
		HttpSession session;
		
	    switch(parameter){
	    case 0:
	    	try {
		    	for(String s : ListQ)	
					out.print(s+",");
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	break;
	    case 1:
	    	try {
	    		session = activeSession.getDriver();
	    		if(session == null){
	    			out.print("No one");
	    		} else {
					out.print(session.getAttribute("nickName"));
	    		}
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    }
	    
	    
		
		return EVAL_PAGE;
	}
	
	public int doEndTag() throws JspException{
		return EVAL_PAGE;
	}

}

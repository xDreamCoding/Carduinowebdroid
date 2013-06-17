package de.carduinodroid.web;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import de.carduinodroid.shared.*;

public class QueueTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	
	public int doStartTag() throws JspException{
		
		JspWriter out = pageContext.getOut();
		String[] List = new  String [waitingqueue.getLength()];
	    List = waitingqueue.getAllSessions();
		
	    try {
	    	for(String s : List)	
				out.print(s+",");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return EVAL_PAGE;
	}
	
	public int doEndTag() throws JspException{
		return EVAL_PAGE;
	}

}

package de.carduinodroid.web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class QueueTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	
	public int doStartTag() throws JspException{
		
		return EVAL_PAGE;
	}
	
	public int doEndTag() throws JspException{
		return EVAL_PAGE;
	}

}

package de.carduinodroid.web;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * \brief This class handles the Session related custom tags.
 * \details Currently there is only the isAdmin tag implemented but this can easily be expanded
 * @author Christoph Braun
 *
 */

public class SessionTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	/**
	 * \brief Starting tag handler
	 * @return EVAL_PAGE. Tells the Server to continue evaluating Tags.
	 * @throws Exception Throws a default Jsp Exception with full stacktrace.
	 */
	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			out.print(pageContext.getSession().getAttribute("isAdmin"));		
		} catch (IOException e){
			throw new JspException("Error: " + e.getMessage());
		}
		return EVAL_PAGE;
	}
	
		
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
}

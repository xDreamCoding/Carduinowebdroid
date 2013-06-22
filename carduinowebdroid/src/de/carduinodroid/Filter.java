package de.carduinodroid;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.carduinodroid.utilities.Log;

/**
 * Servlet Filter implementation class Filter
 */
@WebFilter("/*")

/**
 * \brief This Class is used to redirect after a Servlet-Request
 * @author Alexander Rose
 * @author Sven-Leonhard Weiler
 * @author Michael Röding
 */

public class Filter implements javax.servlet.Filter {
	
	FilterConfig config;
	Log log;
	final boolean DEBUG = false;
	
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if(DEBUG) System.out.println("filter");		
		
		config.getServletContext().getRequestDispatcher("/main").include(request, res);
		
		boolean authorized = false;
		boolean staticRequest = false;
		boolean websocketRequest = false;
		String target = "index";

		if(request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();
			//String ipAdress = req.getRemoteAddr();
			//System.out.println(ipAdress);
			if(DEBUG) System.out.println("-> " + req.getRequestURI());

			staticRequest = req.getRequestURI().startsWith(req.getContextPath() + "/static");
			websocketRequest = req.getRequestURI().startsWith(req.getContextPath() + "/websocket");

			if(session.getAttribute("nickName") != null && ((String)session.getAttribute("nickName")) != "") {
				authorized = true;
				target = "main";
				if(req.getRequestURI().endsWith("admin.jsp") && (boolean)session.getAttribute("isAdmin")) 
					target = "admin";
			}
			if(req.getRequestURI().endsWith("about.jsp"))
				target = "about";
			else if(req.getRequestURI().endsWith("impress.jsp"))
				target = "impress";
			else if(req.getRequestURI().endsWith("queue.jsp"))  
				target = "queue";  
		}
		if(staticRequest || websocketRequest) 
			chain.doFilter(request, res);
		else {
			config.getServletContext().getRequestDispatcher("/WEB-INF/" + target + ".jsp").forward(request, res);			
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		config = fConfig;
		System.out.println("init filter");
		
		log = (Log)config.getServletContext().getAttribute("log");
	}
	
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}


}

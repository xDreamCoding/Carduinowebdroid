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

/**
 * Servlet Filter implementation class Filter
 */
@WebFilter("/*")
public class Filter implements javax.servlet.Filter {
	
	FilterConfig config;

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		// place your code here
		System.out.println("filter");
		
		boolean authorized = false;
		boolean staticRequest = false;
		
		if(request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();
			
			staticRequest = req.getRequestURI().startsWith("static");
			
			if(session.getAttribute("name") != null) {
				authorized = true;
			}
		}
		
		if(authorized || staticRequest) {
			chain.doFilter(request, res);
		} else {
			config.getServletContext().getRequestDispatcher("/WEB-INF/login.jsp").forward(request, res);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		config = fConfig;
		System.out.println("initfilter");
	}
	
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}


}

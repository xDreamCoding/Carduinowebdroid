package de.carduinodroid;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
			
			Map<String, String[]> m = req.getParameterMap();
			
			Iterator<Entry<String, String[]>> entries = m.entrySet().iterator();
			while (entries.hasNext()) {
			    Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) entries.next();
			    String key = (String)entry.getKey();
			    String[] value = (String[])entry.getValue();
			    System.out.println("Key = " + key + ", Value = " + value[0]);
			}
			
			System.out.println("-> " + req.getRequestURI());
			
			staticRequest = req.getRequestURI().startsWith(req.getContextPath() + "/static");
			
			if(session.getAttribute("name") != null) {
				authorized = true;
			}
		}
		
		if(authorized || staticRequest) {
			chain.doFilter(request, res);
		} else {
			config.getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(request, res);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		config = fConfig;
		System.out.println("init filter");
	}
	
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}


}

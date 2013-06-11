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

import de.carduinodroid.shared.User;
import de.carduinodroid.shared.activeSession;
import de.carduinodroid.utilities.DBConnector;
import de.carduinodroid.utilities.LogNG;
import de.carduinodroid.shared.Warteschlange;

/**
 * Servlet Filter implementation class Filter
 */
@WebFilter("/*")
public class Filter implements javax.servlet.Filter {
	
	FilterConfig config;
	LogNG log;
	
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
			
			System.out.println("-> " + req.getRequestURI());
			
			Map<String, String[]> m = req.getParameterMap();
			Iterator<Entry<String, String[]>> entries = m.entrySet().iterator();
			while (entries.hasNext()) {
			    Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) entries.next();
			    String key = (String)entry.getKey();
			    String[] value = (String[])entry.getValue();
			    System.out.println("Key = " + key + ", Value = " + value[0]);
			}
			
			String SessionID = session.getId();
			if(m.size() > 0 && m.containsKey("action")) {
				switch((String)m.get("action")[0])  {
				case "login":
					if(!m.containsKey("loginName") || !m.containsKey("password"))
						break;
					
					DBConnector db = (DBConnector)config.getServletContext().getAttribute("database");
					String userID, pw;
					userID = (String)m.get("loginName")[0];
					pw = (String)m.get("password")[0];
					User u = db.loginUser(userID, pw);
					
					if(u == null)
						break;
					
					session.setAttribute("name", u.getNickname());
					System.out.println("user " + u.getNickname() + " has logged in");
					break;
				case "enqueue":
					de.carduinodroid.shared.Warteschlange.insertUser(SessionID);
					log.logQueue((String)m.get("loginName")[0], Integer.parseInt(SessionID));
					break;
				case "dequeue":
					de.carduinodroid.shared.Warteschlange.deleteTicket(SessionID);
					break;
				case "NextUser":
					String nextUserID = de.carduinodroid.shared.Warteschlange.getNextUser();
					//TODO wohin soll der übergeben werden
					break;
				case "watchDriver":
					activeSession.insertSession(SessionID);
					config.getServletContext().getRequestDispatcher("/WEB-INF/main.jsp").forward(request, res);
					//TODO unterschied zwischen watch a driver und login
				}
			}
			
			staticRequest = req.getRequestURI().startsWith(req.getContextPath() + "/static");
			
			if(session.getAttribute("name") != null) {
				activeSession.insertSession(SessionID);
				authorized = true;
			}
		}
		
		if(authorized) {
			config.getServletContext().getRequestDispatcher("/WEB-INF/main.jsp").forward(request, res);
		} else if(staticRequest) {
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
		
		log = (LogNG)config.getServletContext().getAttribute("log");
	}
	
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}


}

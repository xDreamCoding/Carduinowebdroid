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
import de.carduinodroid.shared.waitingqueue;

/**
 * Servlet Filter implementation class Filter
 */
@WebFilter("/*")

/**
 * \brief This Class is used to catch ServletRequest e.g. Login,Logout or enqueue into the waiting queue
 * @author Alexander Rose, wer noch?
 *
 */

public class Filter implements javax.servlet.Filter {
	
	FilterConfig config;
	LogNG log;
	
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		// place your code here
		System.out.println("filter");		
		
		config.getServletContext().getRequestDispatcher("/main").include(request, res);
		
		boolean authorized = false;
		boolean isAdmin = false;
		boolean staticRequest = false;
		String target = "index";
		
		//DBConnector db = (DBConnector)config.getServletContext().getAttribute("database");
		
		if(request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();
			//String ipAdress = req.getRemoteAddr();
			//System.out.println(ipAdress);
			System.out.println("-> " + req.getRequestURI());
			
//			Map<String, String[]> m = req.getParameterMap();
//			Iterator<Entry<String, String[]>> entries = m.entrySet().iterator();
//			while (entries.hasNext()) {
//			    Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) entries.next();
//			    String key = (String)entry.getKey();
//			    String[] value = (String[])entry.getValue();
//			    System.out.println("Key = " + key + ", Value = " + value[0]);
//			}
//			///TODO \todo Sessions sind wirklich Strings werden aber später nach int gecastet
//			String SessionID = session.getId();
//			if(m.size() > 0 && m.containsKey("action")) {
//				
//				switch((String)m.get("action")[0])  {
//				case "login":
//					if(!m.containsKey("loginName") || !m.containsKey("password"))
//						break;
//					
//					String userID, pw;
//					userID = (String)m.get("loginName")[0];
//					pw = (String)m.get("password")[0];
//					User u = db.loginUser(userID, pw);
//					
//					if(u == null)
//						break;
//					
//					///TODO \todo session attribute (Rechte der user)
//					session.setAttribute("isAdmin", u.isAdmin());
//					session.setAttribute("isUser", u.isUser());
//					session.setAttribute("name", u.getNickname());
//					System.out.println("user " + u.getNickname() + " has logged in");
//					activeSession.insertSession(SessionID,ipAdress,userID);
//					break;
//				case "enqueue":					
//					User user = db.getUserBySession(activeSession.getSessionInt(SessionID));
//					if (user == null){
//						System.out.println("User nicht gefunden");
//						break;
//					}
//					if (user.isGuest() == true) return;
//					waitingqueue.insertUser(SessionID);
//					log.logQueue(user.getUserID(), activeSession.getSessionInt(SessionID));
//					break;
//				case "dequeue":
//					waitingqueue.deleteTicket(SessionID);
//					break;
//				case "NextUser":
//					//String nextUserID = waitingqueue.getNextUser();
//					///TODO \todo wohin soll der übergeben werden
//					break;
//				case "watchDriver":
//					userID = "guest" + System.currentTimeMillis();
//					activeSession.insertSession(SessionID, ipAdress, userID);
//					config.getServletContext().getRequestDispatcher("/WEB-INF/main.jsp").forward(request, res);
//					break;
//				case "toMainPage":
//					config.getServletContext().getRequestDispatcher("/WEB-INF/main.jsp").forward(request, res);
//					break;
//				case "toAdminPage":
//					User user2 = db.getUserBySession(activeSession.getSessionInt(SessionID));
//					if (user2.isAdmin() == true){
//						config.getServletContext().getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, res);
//					}
//					break;
//				case "logout":
//					activeSession.deleteSession(SessionID);
//					waitingqueue.deleteTicket(SessionID);
//					break;
//				}
//			}
			
			staticRequest = req.getRequestURI().startsWith(req.getContextPath() + "/static");
			
			if(session.getAttribute("nickName") != null && ((String)session.getAttribute("nickName")) != "") {
				authorized = true;
				target = "main";
				if((boolean)session.getAttribute("isAdmin")) {
					isAdmin = true;
					if(req.getRequestURI().endsWith("admin.jsp")) 
						target = "admin";
				}
			}
		}
		if(staticRequest) 
			chain.doFilter(request, res);
		else {
			if(!authorized) 
				config.getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(request, res);
			else 
				config.getServletContext().getRequestDispatcher("/WEB-INF/" + target + ".jsp").forward(request, res);			
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

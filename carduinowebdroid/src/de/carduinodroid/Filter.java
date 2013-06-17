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

import de.carduinodroid.utilities.LogNG;

/**
 * Servlet Filter implementation class Filter
 */
@WebFilter("/*")

/**
 * \brief This Class is used to catch ServletRequest e.g. Login,Logout or enqueue into the waiting queue
 * @author Alexander Rose
 * @author Sven-Leonhard Weiler
 * @author Michael Röding
 */

public class Filter implements javax.servlet.Filter {
	
	FilterConfig config;
	LogNG log;
	
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		System.out.println("filter");		
		
		config.getServletContext().getRequestDispatcher("/main").include(request, res);
		
		boolean authorized = false;
		boolean staticRequest = false;
		boolean chatRequest = false;
		String target = "index";

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
			
//			Enumeration<String> sessionKeys = session.getAttributeNames();
//		    while(sessionKeys.hasMoreElements())
//		    {
//		        String value=(String) sessionKeys.nextElement();
//		        System.out.println(value + " -> " + session.getAttribute(value));
//		    }

			staticRequest = req.getRequestURI().startsWith(req.getContextPath() + "/static");
			chatRequest = req.getRequestURI().startsWith(req.getContextPath() + "/chat");

			if(session.getAttribute("nickName") != null && ((String)session.getAttribute("nickName")) != "") {
				authorized = true;
				target = "main";
				if(req.getRequestURI().endsWith("admin.jsp") && (boolean)session.getAttribute("isAdmin")) 
					target = "admin";				
			}
		}
		if(staticRequest || chatRequest) 
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

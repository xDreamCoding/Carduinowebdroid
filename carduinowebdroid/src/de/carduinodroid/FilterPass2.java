package de.carduinodroid;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.carduinodroid.shared.User;
import de.carduinodroid.shared.activeSession;
import de.carduinodroid.shared.waitingqueue;
import de.carduinodroid.shared.User.Right;
import de.carduinodroid.utilities.CarControllerWrapper;
import de.carduinodroid.utilities.DBConnector;
import de.carduinodroid.utilities.Config.Options;
import de.carduinodroid.utilities.Log;

@WebServlet(loadOnStartup=1, value = "/filterPass2")
public class FilterPass2  extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static boolean DEBUG = false;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FilterPass2() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /*
     * what is get and what is post?
     * 
     * get:
     * 
     * post:
     * 	- login
     * 	- enqueue
     * 	- dequeue
     * 	- watchDriver
     * 	-
     * 
     * NO REDIRECT FROM HERE!
     */

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// nothing to do here
		
		if(DEBUG) System.out.println("doGet");
		
//			String[] args1 = null;							// new
//		
//  	  		if ( !BildSender.Runner) {						// new
//  	  		System.out.println("Starte CarStream ");
//  	  		try {
//  	  			BildSender.main(args1);
//  	  			System.out.println("CarStream gestartet");
//  	  		} catch  (final Exception ex) {
//  	  		System.out.println("Fehler beim Starten des CarStreams");
//  	  		}
//  	  	}													// new
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(DEBUG) System.out.println("doPost");
		
		Log log = (Log)request.getServletContext().getAttribute("log");
		
		if(request instanceof HttpServletRequest) {			
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();			

			Map<String, String[]> postParameterMap = req.getParameterMap();
			if(DEBUG) {
				Iterator<Entry<String, String[]>> entries = postParameterMap.entrySet().iterator();
				while (entries.hasNext()) {
				    Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) entries.next();
				    String key = (String)entry.getKey();
				    String[] value = (String[])entry.getValue();
				    System.out.println("Key = " + key + ", Value = " + value[0]);
				}
			}
			
			if(postParameterMap.size() > 0 && postParameterMap.containsKey("action")) {
				String SessionID = session.getId();
				String ipAdress = req.getRemoteAddr();
				DBConnector db = (DBConnector)request.getServletContext().getAttribute("database");
				
				switch((String)postParameterMap.get("action")[0])  {				
				case "login":
					if(!postParameterMap.containsKey("loginName") || !postParameterMap.containsKey("password"))
						break;
					
					String userID, pw;
					userID = (String)postParameterMap.get("loginName")[0];
					pw = (String)postParameterMap.get("password")[0];
					User u = db.loginUser(userID, pw);
					
					if(u == null)
						break;
					
					int ID = activeSession.insertSession(SessionID, ipAdress, userID);
					if (ID == -1)
						break;
					
					session.setAttribute("isAdmin", u.isAdmin());
					session.setAttribute("isUser", u.isUser());
					session.setAttribute("nickName", u.getNickname());
					session.setAttribute("userId", u.getUserID());
					session.setAttribute("dbSessionID", ID);
					
					//System.out.println("user " + u.getNickname() + " has logged in");
					break;
				case "enqueue":					
					if (activeSession.isActive(SessionID) == false){
						session.removeAttribute("nickName");
						break;
					}
					User user = db.getUserBySession(activeSession.getSessionInt(SessionID));
					if (user == null){
						System.out.println("User nicht gefunden");
						break;
					}
					if (user.isGuest() == true) return;
					waitingqueue.insertUser(SessionID);
					log.logQueue(user.getUserID(), activeSession.getSessionInt(SessionID));
					break;
				case "dequeue":
					if (activeSession.isActive(SessionID) == false){
						session.removeAttribute("nickName");
						break;
					}
					waitingqueue.deleteTicket(SessionID);
					break;
				case "NextUser":
					//String nextUserID = waitingqueue.getNextUser();
					///TODO \todo wohin soll der übergeben werden
					break;
				case "watchDriver":
					userID = "gue" + System.currentTimeMillis();
					db.loginGuest(userID);
					ID = activeSession.insertSession(SessionID, ipAdress, userID);
					if (ID == -1){
						log.writelogfile("error creating session for guest " + userID);
						break;
					}
					session.setAttribute("isAdmin", false);
					session.setAttribute("isUser", false);
					session.setAttribute("nickName", userID);
					session.setAttribute("userId", userID);
					session.setAttribute("dbSessionID", ID);
					break;
				case "logout":
					activeSession.deleteSession(SessionID);
					waitingqueue.deleteTicket(SessionID);
					session.removeAttribute("nickName");
					///TODO \todo logout = ich lösche ein paar sachen und das wars? session? rechte? zurück zum index?				

					if ( BildSender.Runner) {							// new
			  	  		System.out.println("Stoppe CarStream ");		// new
			  	  		BildSender.Stop();								// new
			  	  		System.out.println("CarStream beendet");		// new
			  	  	}													// new
					
					break;
				case "connect":
					if (activeSession.isActive(SessionID) == false){
						session.removeAttribute("nickName");
						break;
					}
					String ip = ((Options)request.getServletContext().getAttribute("options")).carduinodroidIP;
					if(ip == "") {
						log.writelogfile("unable to connect to carduinodroid because ip is not set");
						break;
					}
					try {
						CarControllerWrapper ccw = CarControllerWrapper.getCarController();
						ccw.connect(ip);
					} catch (Exception e) {
						log.writelogfile(e.getMessage());
					}
					break;
				case "edituser":
					if (session.getAttribute("isAdmin").equals(false)){
						System.out.println("Admin-Rechte werden für diese Operation benötigt");
						break;
					}
					if (!(postParameterMap.containsKey("userid")) || !(postParameterMap.containsKey("nickname"))){
						System.out.println("Feld unvollständig");
						break;
					}
					boolean isAdmin;
					userID = (String) postParameterMap.get("userid")[0]; 
					String Nickname = (String) postParameterMap.get("nickname")[0];
					if (postParameterMap.containsKey("rights") == false){
						isAdmin = false;
					}
					else{
						isAdmin = true;
					}
					if (userID == db.getUserIdBySession(activeSession.getSessionInt(session.getId())) && (!isAdmin)){
						System.out.println("Man kann sich nicht selbst das Admin recht entziehen");
						break;
					}
					if (isAdmin){
						System.out.println("Admin");
						db.editUser(userID, Nickname, Right.ADMIN);
					}
					else{
						db.editUser(userID, Nickname, Right.USER);
						System.out.println("User");
					}
					
					break;
				case "adduser":
					if (session.getAttribute("isAdmin").equals(false)){
						System.out.println("Admin-Rechte werden für diese Operation benötigt");
						break;
					}
					
					if (!(postParameterMap.containsKey("userid")) || !(postParameterMap.containsKey("nickname")) || !(postParameterMap.containsKey("password"))){
						System.out.println("Feld unvollständig");
						break;
					}
					
					userID = (String) postParameterMap.get("userid")[0]; 
					Nickname = (String) postParameterMap.get("nickname")[0];
					if (postParameterMap.containsKey("rights")){
						isAdmin = true;
					}
					else{
						isAdmin = false;
					}
					String password = (String) postParameterMap.get("password")[0];
					
					if (isAdmin){
						db.createUser(userID, Nickname, password, Right.ADMIN);
					}
					else{
						db.createUser(userID, Nickname, password, Right.USER);
					}
					break;
				default:
					//HOW COULD DIS HAPPEN?
					break;
				}
			}
		}
	}

}

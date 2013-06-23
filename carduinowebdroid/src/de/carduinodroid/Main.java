package de.carduinodroid;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.carduinodroid.shared.*;
import de.carduinodroid.utilities.*;
import de.carduinodroid.utilities.Config.Options;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.Timer;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * Servlet implementation class Main
 */
@WebServlet(loadOnStartup=1, value = "/main")

/**
 * \brief This Class is used to handle user-timeouts and to log GPS in a given interval and to handle the waiting queue and to handle Servlet Requests
 * @author Alexander Rose
 *
 */

public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;   
	private static Timer caretaker;
	private static Timer GPSLog;
	private static TimerTask GPSLogger;
	private static TimerTask Session;
	private static TimerTask action;
	private static ArrayList<String> aliveSessions;
	static Log log;
	static int driveID;
	static int Fahrzeit, gpsLogInterval;
	static Options opt;
	static boolean flag;
	static String aktSessionID;
	
	final static boolean DEBUG = false;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Main() {
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
		
			String[] args1 = null;							// new
		
  	  		if ( !BildSender.Runner) {						// new
  	  		System.out.println("Starte CarStream ");
  	  		try {
  	  			BildSender.main(args1);
  	  			System.out.println("CarStream gestartet");
  	  		} catch  (final Exception ex) {
  	  		System.out.println("Fehler beim Starten des CarStreams");
  	  		}
  	  	}													// new
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(DEBUG) System.out.println("doPost");
		
		if(request instanceof HttpServletRequest) {			
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();			

			Map<String, String[]> m = req.getParameterMap();
			if(DEBUG) {
				Iterator<Entry<String, String[]>> entries = m.entrySet().iterator();
				while (entries.hasNext()) {
				    Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) entries.next();
				    String key = (String)entry.getKey();
				    String[] value = (String[])entry.getValue();
				    System.out.println("Key = " + key + ", Value = " + value[0]);
				}
			}
			
			if(m.size() > 0 && m.containsKey("action")) {
				String SessionID = session.getId();
				String ipAdress = req.getRemoteAddr();
				DBConnector db = (DBConnector)request.getServletContext().getAttribute("database");
				
				switch((String)m.get("action")[0])  {				
				case "login":
					if(!m.containsKey("loginName") || !m.containsKey("password"))
						break;
					
					String userID, pw;
					userID = (String)m.get("loginName")[0];
					pw = (String)m.get("password")[0];
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
				default:
					//HOW COULD DIS HAPPEN?
					break;
				}
			}
		}
	}

	/** 
	 * \brief refreshes the options needed in this class (Fahrzeit and GPS interval)
	 * @param opt Options object
	 */
	
	public static void refresh(Options opt){
    	Fahrzeit = opt.fahrZeit;
    	gpsLogInterval = opt.logGPSInterval;
    	flag = true;
    }
	
	/** 
	 * \brief deletes all Timers and TimerTasks and also deletes all active sessions
	 */
	
	public static void shutDown(){
		if(DEBUG) System.out.println("Shut-Down main");
		Session.cancel();
		action.cancel();
		GPSLogger.cancel();
		GPSLog.cancel();
		activeSession.deleteAll();
	}
    
    public static void restartTimer(){
    	caretaker.cancel();
    	caretaker = new Timer();
    	caretaker.schedule(new de.carduinodroid.Dummy(action), 1000, 60000*Fahrzeit);		
    }
	
	public static void receivedPing(String SessionID){
		int index = aliveSessions.indexOf(SessionID);
		aliveSessions.remove(index);
	}
    
    public static void main(Options opt, DBConnector db, Log logng){
    	
    	log = logng;
    	aliveSessions = new ArrayList<String>();
    	
    	Session = new TimerTask(){
			public void run(){
							
				for (int i = 0; i < aliveSessions.size(); i++){
					activeSession.deleteSession(aliveSessions.get(i));
					waitingqueue.deleteTicket(aliveSessions.get(i));
				}
				
				String[] Sessions = new String[activeSession.getAllSessions().length];
				Sessions = activeSession.getAllSessions();
			
				for(int i = 0; i < Sessions.length; i++){
					aliveSessions.add(Sessions[i]);
				}
			   			
			}
		};
    	
		Timer Sessionhandle = new Timer();
		Sessionhandle.schedule(Session, 10, 5000);
		Fahrzeit = opt.fahrZeit;
		gpsLogInterval = opt.logGPSInterval;
		if(DEBUG) System.out.println("Main-function");
		
		try {
			action = new TimerTask() {
				DBConnector db = new DBConnector();
				public void run() {
			    	if(flag){
			    		caretaker.cancel();
						caretaker = new Timer();
			    		caretaker.schedule(new de.carduinodroid.Dummy(action), 60000*Fahrzeit, 60000*Fahrzeit);
			    		flag = false;
			    	}
					
			    	activeSession.resetDriver();
			    	
					if (waitingqueue.isEmpty() == true){
						caretaker.cancel();
						caretaker = new Timer();
						caretaker.schedule(new de.carduinodroid.Dummy(action), 1000, 60000*Fahrzeit);				
						return;
					}
					else{
						String aktSessionID = waitingqueue.getNextUser();
						driveID = db.startDrive(db.getUserIdBySession(activeSession.getSessionInt(aktSessionID)));
						activeSession.setDriver(aktSessionID);
						///TODO \todo Fahrrechte;

						}
					}
			    
			};
		} catch (Exception e) {
			log.writelogfile(e.getMessage());
			e.printStackTrace();
		}
	
		
		
		caretaker = new Timer();
		caretaker.schedule(action, 100, 60000*Fahrzeit);  
		
		try {
			GPSLogger = new TimerTask(){		
				public void run(){
					String longitude = CarControllerWrapper.getLongitude();
					String latitude = CarControllerWrapper.getLatitude();
					if (longitude == null || latitude == null){
						if(DEBUG) System.out.println("GPS: N/A");
					} else
						log.logGPS(driveID, longitude, latitude);
				}
			};
		} catch (Exception e) {
			log.writelogfile(e.getMessage());
			e.printStackTrace();
		}
		GPSLog = new Timer();
		GPSLog.schedule(GPSLogger, 10, gpsLogInterval * 1000);
    }   
    
}

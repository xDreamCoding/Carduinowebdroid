package de.carduinodroid;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import de.carduinodroid.shared.*;
import de.carduinodroid.utilities.*;
import de.carduinodroid.utilities.Config.Options;
import de.carduinodroid.utilities.LogNG;
import java.util.TimerTask;
import java.util.Timer;
import java.util.ArrayList;

/**
 * Servlet implementation class Main
 */
@WebServlet(loadOnStartup=1, value = "/main")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;   
	private static Timer caretaker;
	private static Timer GPSLog;
	private static TimerTask GPSLogger;
	private static TimerTask Session;
	private static TimerTask action;
	private static ArrayList<String> aliveSessions;
	static LogNG log;
	static int driveID;
	static int Fahrzeit;
	static Options opt;
	static boolean flag;
	static String aktSessionID;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Main() {
        super();
        // TODO Auto-generated constructor stub
        System.out.println("Main");
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
     */

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("doGet");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("doPost");
	}

    public static void refresh(Options opt){
    	Fahrzeit = opt.fahrZeit;
    	flag = true;
    }
	
	public static void shutDown(){
		System.out.println("Shut-Down main");
		Session.cancel();
		action.cancel();
		GPSLogger.cancel();
		GPSLog.cancel();
	}
    
    public static void main(Options opt,DBConnector db,LogNG logng){
    	
    	log = logng;
    	aliveSessions = new ArrayList<String>();
    	
//    	Session = new TimerTask(){
//			public void run(){
//							
//				for (int i = 0; i < aliveSessions.size(); i++){
//					activeSession.deleteSession(aliveSessions.get(i));
//					waitingqueue.deleteTicket(aliveSessions.get(i));
//				}
//				
//				String[] Sessions = new String[activeSession.getAllSessions().length];
//				Sessions = activeSession.getAllSessions();
//			
//				for(int i = 0; i < Sessions.length; i++){
//					aliveSessions.add(Sessions[i]);
//					//TODO sende Nachricht an user und versuche diese wieder zu Empfangen
//				}
//			
//				//TODO wenn Nachrichten ankommen entferne User aus aliveSessions
//			}
//		};
//    	
//		Timer Sessionhandle = new Timer();
//		Sessionhandle.schedule(Session, 10, 5000);
		Fahrzeit = opt.fahrZeit;
		System.out.println("Main-function");
		
		action = new TimerTask() {
			public void run() {
            	if(flag){
    				caretaker.cancel();
    				caretaker = new Timer();
            		caretaker.schedule(new de.carduinodroid.Dummy(action), 60000*Fahrzeit, 60000*Fahrzeit);
            		flag = false;
            	}
				
				if (waitingqueue.isEmpty() == true){
					caretaker.cancel();
					caretaker = new Timer();
					caretaker.schedule(new de.carduinodroid.Dummy(action), 1000, 60000*Fahrzeit);				
    				return;
    			}
    			else{
    				DBConnector db = null;
					try {
						db = new DBConnector();
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
    				String aktSessionID = waitingqueue.getNextUser();
    				driveID = db.startDrive(db.getUserIdBySession(activeSession.getSessionInt(aktSessionID)));
    				//TODO Fahrrechte;

    				}
    			}
            
		};
	
		
		
		caretaker = new Timer();
		caretaker.schedule(action, 100, 60000*Fahrzeit);  
		
		GPSLogger = new TimerTask(){
			public void run(){
				CarControllerWrapper Controller = new CarControllerWrapper(log);
				String longitude = Controller.getLongitude();
				String latitude = Controller.getLatitude();
				if (longitude == null || latitude == null){
					System.out.println("GPS does not work");
					return;
				}
				log.logGPS(driveID, longitude, latitude);
			}
		};
		GPSLog = new Timer();
		GPSLog.schedule(GPSLogger, 10, 5000);
    }   
    
}

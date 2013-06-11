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
import java.util.TimerTask;
import java.util.Timer;

/**
 * Servlet implementation class Main
 */
@WebServlet(loadOnStartup=1, value = "/main")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;   
	private static Timer caretaker;
	private static Timer timeout;
	private static Timer Sessionhandle;
	private static TimerTask Session;
	private static TimerTask kick;
	private static TimerTask action;
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
	
	public static void main(Options opt,DBConnector db){
		
		Sessionhandle = new Timer();
		Sessionhandle.schedule(Session, 0, 5000);
		Fahrzeit = opt.fahrZeit;
		System.out.println("Main-function");
		
		action = new TimerTask() {
			public void run() {
            	if(flag){
            		caretaker.cancel();
    				caretaker.schedule(action, 60000*Fahrzeit, 60000*Fahrzeit);
    				flag = false;
            	}
				
				if (de.carduinodroid.shared.Warteschlange.isEmpty() == true){
            		caretaker.cancel();
    				caretaker.schedule(action, 1000, 60000*Fahrzeit);
    			}
    			else{
    				DBConnector db = null;
					try {
						db = new DBConnector();
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
    				String aktSessionID = de.carduinodroid.shared.Warteschlange.getNextUser();
    				db.startDrive(db.getUserIdBySession(Integer.parseInt(aktSessionID)));
    				//TODO Fahrrechte;

    				}
    			}
            
		};
	
		Session = new TimerTask(){
			public void run(){
				
			
				String[] Sessions = new String[activeSession.getAllSessions().length];
				Sessions = activeSession.getAllSessions();
			
				for(int i = 0; i < Sessions.length; i++){
					aktSessionID = Sessions[i];
					//TODO sende Nachricht an user und versuche diese wieder zu Empfangen
					kick = new TimerTask(){
			    		public void run(){
			    			activeSession.deleteSession(aktSessionID);
			    		}
			    	};
					timeout = new Timer();
					timeout.schedule(kick, 10000);
					//if(msg arrives) timeout.cancel();
				}
			}
		};
		
		caretaker = new Timer();
        caretaker.schedule(action, 100, 60000*Fahrzeit);    
	}
		//TODO log GPS
}

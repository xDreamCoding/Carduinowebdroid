package de.carduinodroid;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import de.carduinodroid.desktop.Controller.Controller_Computer;
import de.carduinodroid.desktop.Model.GPSTrack;
import de.carduinodroid.desktop.Model.Log;
import de.carduinodroid.utilities.Config;
import de.carduinodroid.utilities.Config.Options;
import de.carduinodroid.utilities.DBConnector;

@WebListener
public class MyServletContextListener implements ServletContextListener {

	private ServletContext context;
	private Log log;

	/*This method is invoked when the Web Application has been removed 
	and is no longer able to accept requests
	*/

	public void contextDestroyed(ServletContextEvent event)
	{
		log.writelogfile("contextDestroyed.");
		this.context = null;
		
		DBConnector db = (DBConnector)event.getServletContext().getAttribute("database");
		db.shutDown();
	}


	//This method is invoked when the Web Application
	//is ready to service requests

	public void contextInitialized(ServletContextEvent event)
	{
		context = event.getServletContext();
		log = new Log();
		context.setAttribute("log", log);
		log.writelogfile("contextInitialized. Log instanciated.");
		
		GPSTrack gps = new GPSTrack();
		log.writelogfile("GPSTracker instanciated.");
		
		Controller_Computer controller = new Controller_Computer(log, gps);
		context.setAttribute("controller", controller);
		log.writelogfile("Controller_Computer instanciated.");
		
		Config config = new Config(log, context.getRealPath("/WEB-INF/config"));
		config.readOptions();
		//context.setAttribute("config", config);
		
		Options options = config.getOptions();
		if(options.dbAddress == null) {
			config.setDefault();
			options = config.getOptions();
		}
		
		context.setAttribute("options", options);
		log.writelogfile("Options loaded");
		
		
		DBConnector db = new DBConnector(log, options);
		context.setAttribute("database", db);
		
		//db.dbTest();		
	}
}

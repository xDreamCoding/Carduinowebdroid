package de.carduinodroid.utilities;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Servlet implementation class CSServlet
 */
@WebServlet(description = "Servlet für den Bildstream", urlPatterns = { "/CarStream" })
public class CSServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CSServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
    public void doGet( HttpServletRequest requ, HttpServletResponse resp )
    	throws ServletException, IOException
    	{
    	    				
    	resp.setContentType( "text/html" );
    	PrintWriter out = resp.getWriter();
    	out.println( "<html>" );
    	out.println( "<iframe src=\"http://localhost:8889/\" width=\"640\" height=\"480\" scrolling=\"no\">");
    	out.println( "<iframe/>");
    	out.println( "</html>" );
    	out.close();
    		      
    	final Logger logger = Logger.getLogger(BildSender.class.getName());
    	// FrameProducer stellt die Bilder, die Gesendet werden zur Verfügung
    	final FrameProducer frameProducer = new FrameProducer();
    	// MJPGServer Sendet die Bilder von FrameProducer an Port 8889
    	// kann in Firefox direkt aufgerufen werden
    	final MJPGServer MJPGServer = new MJPGServer(frameProducer, 8889);
    	// RawFrameServer sorgt dafür, daß der Buffer mit dem richtigen Frame gefüllt wird
    	// Kontrollieren kann man den Datenstrom an Port 8888
    	final RawFrameServer rawFrameServer = new RawFrameServer(frameProducer, 8888);
    	// EXS ist die Javainterne Threadverwaltung
    	final ExecutorService EXS = Executors.newCachedThreadPool();
    	   
    	EXS.submit(frameProducer);
    	EXS.submit(MJPGServer);
    	//EXS.submit(rawFrameServer);
    		      
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

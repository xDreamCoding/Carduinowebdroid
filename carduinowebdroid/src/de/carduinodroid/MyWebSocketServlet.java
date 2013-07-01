package de.carduinodroid;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

import de.carduinodroid.shared.activeSession;
import de.carduinodroid.utilities.CarControllerWrapper;
import de.carduinodroid.utilities.Log;

/**
 * \brief This class is used to handle websocket connection.
 *  Handle messages from clients and broadcast them to the rest.
 *  Receive control commands.
 * @author Sven-Leonhard Weiler
 * @author Vincenz Vogel - onBinaryMessage
 */

public class MyWebSocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = 4642341228711151433L;
	public static final String identifierChat = "Ch%:";
	public static final String identifierControl = "Co%:";
	public static final String identifierHeartbeat = "Hb%:";
	
	private static Timer streamTimer = new Timer("streamTimer");

	/**
	 * Connected clients
	 */
	private static List<StreamInbound> clients = new ArrayList<StreamInbound>();
	private static BufferedImage oldImage;
	private static int sameImage = 0;

	@Override
	protected StreamInbound createWebSocketInbound(String string,
			HttpServletRequest hsr) {		
		
		final HttpSession session = hsr.getSession();
		final ServletContext context = hsr.getServletContext();

		final Log log = (Log)context.getAttribute("log");


		/**
		 * Anonymous inner class to create and define MessageInbound object.
		 */
		MessageInbound inbound = new MessageInbound() {

			@Override
			protected void onOpen(WsOutbound outbound) {
				activeSession.insertSocket(session, this);
				int connSize = clients.size();
				System.out.println("onOpen - connections: " + connSize);
			}

			@Override
			protected void onClose(int status) {
				activeSession.deleteSocket(session);
				System.out.println("onClose - status code: " + status);
				if (activeSession.isDriver(session));{
					activeSession.resetDriver();
					QueueManager.restartTimer();
				}
				clients.remove(this);
			}
			
			@Override
			protected void onBinaryMessage(ByteBuffer bb) throws IOException {
				System.out.println("onBinaryMessage");
			}

			@Override
			protected void onTextMessage(CharBuffer cb) throws IOException {			
				String msg = cb.toString();
				
				//System.out.println("onTextMessage: " + msg);
				/**
				 * Heartbeatpart
				 */
				if(msg.startsWith(identifierHeartbeat)) {
					///TODO \todo heartbeatstuff					
					QueueManager.receivedPing(session);
				}
				/**
				 * Controllerpart
				 */
				else if(msg.startsWith(identifierControl) && activeSession.isDriver(session)) {
					msg = msg.substring(identifierControl.length());
					if(msg.startsWith("speed")) {
						System.out.println("speed: " + msg.substring("speed".length()));
						CarControllerWrapper.setSpeed(Integer.valueOf(msg.substring("speed".length())));
					} else if(msg.startsWith("angle")) {
						System.out.println("angle: " + msg.substring("angle".length()));
						CarControllerWrapper.setAngle(Integer.valueOf(msg.substring("angle".length())));
					} else {
						char directionKey = msg.charAt(msg.indexOf(":") + 1);
						char stateKey = msg.charAt(msg.indexOf(":") + 2);		
						
						switch (directionKey) {
						case 'a':
							if(stateKey == 's') CarControllerWrapper.setLeft(true);
							if(stateKey == 'e') CarControllerWrapper.setLeft(false);
							break;
						case 'd':
							if(stateKey == 's') CarControllerWrapper.setRight(true);
							if(stateKey == 'e') CarControllerWrapper.setRight(false);
							break;
						case 'w':
							if(stateKey == 's') CarControllerWrapper.setUp(true);
							if(stateKey == 'e') CarControllerWrapper.setUp(false);
							break;
						case 's':
							if(stateKey == 's') CarControllerWrapper.setDown(true);
							if(stateKey == 'e') CarControllerWrapper.setDown(false);
							break;
						case 'h':
							if(stateKey == 's') CarControllerWrapper.sendSignal();
							break;
						case 'l':
							if(stateKey == 's') CarControllerWrapper.toggleLight();
							break;	
						}
					}
				}
				/**
				 * Chatpart
				 */
				else if(msg.startsWith(identifierChat)){					
					String nickName = (String)session.getAttribute("nickName");

					String msgBody = msg.replaceFirst("Ch%:", "");
					
					System.out.println(nickName + ": " + msgBody);
					
					String userId = (String)session.getAttribute("userId");
					int sessionId = (int)session.getAttribute("dbSessionID");
					log.logChat(userId, sessionId, msgBody);
					
					// Send message to all clients connected
					broadcast(identifierChat + nickName + ": " + msgBody);
				}
			}
		};

		// Collect clients connected
		clients.add(inbound);

		return inbound;
	}
	
	/**
	 * Send a message to all clients connected.
	 * 
	 * @param message
	 */
	private void broadcast(String message) {
		System.out.println(message);
		StreamInbound someClient;
		ListIterator<StreamInbound> iter = clients.listIterator();
		while (iter.hasNext()) {
			someClient = (MessageInbound) iter.next();
			try {
				someClient.getWsOutbound().writeTextMessage(
						CharBuffer.wrap(message));
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * Send a message to all clients connected.
	 * 
	 * @param message
	 */
	private static void broadcastImage(ByteBuffer buffer) {
		System.out.println("Send ByteBuffer");
		StreamInbound someClient;
		ListIterator<StreamInbound> iter = clients.listIterator();

		while (iter.hasNext()) {
			someClient = (MessageInbound) iter.next();
			try {
				someClient.getWsOutbound().writeBinaryMessage( buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * start streamTimer 100ms.
	 */
	public static void startStream() {
		System.out.println("start stream");
		streamTimer.schedule(streamTick, 1000, 100);
	}

	/**
	 * stop streamTimer.
	 */
	public static void stopStream() {
		streamTimer.cancel();
	}
	
	private static TimerTask streamTick = new TimerTask() {	
		@Override
		public void run() {
			int H;
			int B;
						
			/* Test */
//			File file = new File("C:\\Users\\Michael\\Pictures\\im_super_serial__2.png");
//			System.out.println("Datei gefunden, Laenge = " + file.length());
//			ByteBuffer bbu = ByteBuffer.allocate((int) file.length());
//			System.out.println("ByteBuffer erzeugt");
//			final int BYTES_PER_READ = (int) file.length();
//			System.out.println("Bytes ermittelt");
//		    FileInputStream fis = new FileInputStream("C:\\Users\\Michael\\Pictures\\im_super_serial__2.png");
//		    System.out.println("Inputstream erstellt");
//		    int bytesRead = 0;
//		    byte[] buf = new byte[BYTES_PER_READ];
//		    System.out.println("Buf erzeugt");
//		    while (bytesRead != -1)
//		    {
//		        bbu.put(buf, 0, bytesRead);
//		        bytesRead = fis.read(buf);
//		        System.out.println(" " + bytesRead);
//		    }
//		    System.out.println("Bytes kopiert");
//		    fis.close();
//		    
//			BufferedImage image = ImageIO.read( file);
//			B = image.getWidth();
//			H = image.getHeight();
			/* Test */
			/* Work */
			
			BufferedImage image = CarControllerWrapper.getImg();
			if(!image.equals(oldImage)) {
				oldImage = image;
				B = image.getWidth();
				H = image.getHeight();
				/*work */
				
				System.out.println("Bild gelesen, Breite = " + B + " Höhe = " + H);
				// Grösse von pixels = Breite * Höhe * 4 bytes 
				int[] pixels = new int[B * H];
				int BufLeange = B * H * 4 + 8;  // 8 bytes fuer Breite und Hoehe
				System.out.println("pixels angelegt");
				// kopiert alle Pixel von image ( oder nur Bildausschnitt) in ein Feld ( pixels)
		        image.getRGB(0, 0, B, H, pixels, 0, B);
		        System.out.println("Bildgröße bestimmt und RGB- Image erzeugt");
		        
		        System.out.println("Daten für ByteBuffer: " + B * H);
		        ByteBuffer buffer = ByteBuffer.allocate( BufLeange);	
		        buffer.clear();
		        System.out.println("ByteBuffer erstellt = " + BufLeange + " byte");
		        
		        // Bildgroesse im Puffer hinterlegen
		        // Bild- Breite int -> byte[]
		        buffer.put((byte)(B >>> 24));
		        buffer.put((byte)(B >>> 16));
		        buffer.put((byte)(B >>> 8));
		        buffer.put((byte)(B ));
		        System.out.println("Bildbreite in ByteBuffer");
		        
		        // Bild- Hoehe int -> byte[]
		        buffer.put((byte)(H >>> 24));
		        buffer.put((byte)(H >>> 16));
		        buffer.put((byte)(H >>> 8));
		        buffer.put((byte)(H ));
		        System.out.println("Bildhöhe in ByteBuffer");
		        
		        
		        // Bild Pixelinformationen in Puffer hinterlegen
		        for(int y = 0; y < image.getHeight(); y++){
		            for(int x = 0; x < image.getWidth(); x++){
		                int pixel = pixels[y * image.getWidth() + x];
		                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
		                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
		                buffer.put((byte) (pixel & 0xFF));             // Blue component
		                buffer.put((byte) ((pixel >> 24) & 0xFF));     // Alpha component. 
		                												// nicht bei BufferdImage
		            }
		        }
		        
		        			        
		        buffer.flip(); //ByteBuffer speichern
		        System.out.println("ByteBuffer geschrieben");
		        	        
				broadcastImage(buffer);
			} else {
				System.out.println("Same image [" + ++sameImage + "] -> not sending");
			}
		}
	};
}


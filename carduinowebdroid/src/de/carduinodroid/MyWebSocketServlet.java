package de.carduinodroid;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;

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
 */

public class MyWebSocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = 4642341228711151433L;
	public static final String identifierChat = "Ch%:";
	public static final String identifierControl = "Co%:";
	public static final String identifierHeartbeat = "Hb%:";

	/**
	 * Connected clients
	 */
	private static List<StreamInbound> clients = new ArrayList<StreamInbound>();

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
				activeSession.insertSocket(session, this.getWsOutbound());
				int connSize = clients.size();
				System.out.println("onOpen - connections: " + connSize);
			}

			@Override
			protected void onClose(int status) {
				activeSession.deleteSocket(session);
				System.out.println("onClose - status code: " + status);
				activeSession.resetDriver();
				Main.restartTimer();
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
					Main.receivedPing(session.getId());
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
}


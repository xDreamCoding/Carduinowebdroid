package de.carduinodroid;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

import de.carduinodroid.utilities.DBConnector;
import de.carduinodroid.utilities.LogNG;

/**
 * \brief This class is used to receive chatmessages from clients and broadcast them to the rest.
 * @author Sven-Leonhard Weiler
 */

public class ChatServlet extends WebSocketServlet {

	private static final long serialVersionUID = 4642341228711151433L;

	/**
	 * Connected clients
	 */
	private static List<StreamInbound> clients = new ArrayList<StreamInbound>();

	@Override
	protected StreamInbound createWebSocketInbound(String string,
			HttpServletRequest hsr) {		
		
		final HttpSession session = hsr.getSession();
		final ServletContext context = hsr.getServletContext();

		final LogNG log = (LogNG)context.getAttribute("log");

		/**
		 * Anonymous inner class to create and define MessageInbound object.
		 */
		MessageInbound inbound = new MessageInbound() {

			@Override
			protected void onOpen(WsOutbound outbound) {
				int connSize = clients.size();
				System.out.println("onOpen - connections: " + connSize);
			}

			@Override
			protected void onClose(int status) {
				System.out.println("onClose - status code: " + status);
				clients.remove(this);
			}

			@Override
			protected void onBinaryMessage(ByteBuffer bb) throws IOException {
				System.out.println("onBinaryMessage");
			}

			@Override
			protected void onTextMessage(CharBuffer cb) throws IOException {
				System.out.println("onTextMessage");
				
				String nickName = (String)session.getAttribute("nickName");
				
				System.out.println(nickName);
				
				String userId = (String)session.getAttribute("userId");
				//int sessionId = Integer.parseInt(session.getId());
				
				///TODO \todo sessionid woher?
				//log.logChat(userId, sessionId, cb.toString());
				
				// Send message to all clients connected
				broadcast(nickName + ": " + cb.toString());
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


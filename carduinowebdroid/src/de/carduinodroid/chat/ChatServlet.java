package de.carduinodroid.chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

public class ChatServlet extends WebSocketServlet {

	private static final long serialVersionUID = 4642341228711151433L;
	
	public ChatServlet() {
		System.out.println("ChatServlet instanciated");
	}

	/**
	 * Connected clients
	 */
	private static List<StreamInbound> clients = new ArrayList<StreamInbound>();

	@Override
	protected StreamInbound createWebSocketInbound(String string,
			HttpServletRequest hsr) {
		//anonymous inner class
		MessageInbound inbound = new MessageInbound() {

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

				CharBuffer msg = CharBuffer.wrap(cb);
				WsOutbound outbound = getWsOutbound();
				// Send message to client connected
				outbound.writeTextMessage(msg);
				// Send message to all clients connected
				broadcast("Broadcast");
			}

			@Override
			protected void onOpen(WsOutbound outbound) {
				int connSize = clients.size();
				System.out.println("onOpen - connections: " + connSize);
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


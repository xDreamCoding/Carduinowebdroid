package de.carduinodroid.chat;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

@WebServlet(loadOnStartup=1, value = "/chat")
public class ChatServlet extends WebSocketServlet {

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		String name = (String)arg1.getSession().getAttribute("name");
		System.out.println(name + " chat request");
		return new ChatMessageInbound(name);
	}

}

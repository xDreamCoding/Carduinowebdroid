package de.carduinodroid.chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

public class ChatMessageInbound extends MessageInbound {
	
	String name;
	
	public ChatMessageInbound(String name) {
		this.name = name;
	}

	@Override
	protected void onTextMessage(CharBuffer arg0) throws IOException {
		System.out.println(name + " onTextMessage");
	}
	

	@Override
	protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
		// not supported
		throw new UnsupportedOperationException(
				"Binary message not supported.");
	}
	
	@Override
	protected void onOpen(WsOutbound outbound){
		System.out.println(name + " onOpen");
	}
	
	@Override
	protected void onClose(int status){
		System.out.println(name + " onClose");
	}
	
}

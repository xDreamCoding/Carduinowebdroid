package de.carduinodroid;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import de.carduinodroid.utilities.CarControllerWrapper;

import de.carduinodroid.shared.activeSession;

/**
 * \brief This Class is used to reveive Car-Control-Messages from the User
 * @author Alexander Rose
 *
 */

public class ControllerServlet extends WebSocketServlet{

	private static final long serialVersionUID = 1L;
	static String SessionID = null;
	
	public StreamInbound createWebSocketInbound(String protocol) {
		return new ControllMessageInbound();		 
		}
	
	private class ControllMessageInbound extends MessageInbound{
		
		public void onOpen(WsOutbound outbound){
			//TODO wie geht das?
			activeSession.insertSocket(SessionID, outbound);
			

		}
		
		@Override
		public void onClose(int status){
			activeSession.deleteSocket(SessionID);
		}
	
		protected void onTextMessage(CharBuffer buff) throws IOException{
			char key;
			String buffer = buff.toString();
			int index = buffer.indexOf(';');
			String SessionID = null;
			for (int i = 0; i < index; i++){
				SessionID = SessionID + buff.get();
			}

			if (activeSession.isDriver(SessionID) == false){
				return;
			}
			
			for (int i = index; i < buff.length(); i++){
				key = buff.get();
				switch (key){
				case 'a':
					CarControllerWrapper.driveLeft();
					break;
				case 'd':
					CarControllerWrapper.driveRight();
					break;
				case 'w':
					CarControllerWrapper.driveForward();
					break;
				case 's':
					CarControllerWrapper.driveBackward();
					break;
				
				}
			}
		}
	
		protected void onBinaryMessage (ByteBuffer buff) throws IOException{
			
		}
	}

	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest arg1){
		return null;
	}
}

package de.carduinodroid;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
/* work
import de.carduinodroid.utilities.CarControllerWrapper;
work */
/* author: Vincenz Vogel*/

import de.carduinodroid.utilities.CarControllerWrapper;

public class StreamWebSocket extends WebSocketServlet {

		private static final long serialVersionUID = 4642341228711151433L;

		/**
		 * Connected clients
		 */
		private static List<StreamInbound> clients = new ArrayList<StreamInbound>();

		@Override
		protected StreamInbound createWebSocketInbound(String string, HttpServletRequest hsr) {		
			
			
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
					int H;
					int B;
					
					StreamInbound someClient;
					ListIterator<StreamInbound> iter = clients.listIterator();
					
					System.out.println("ws onBinaryMessage");
					
					/* Test */
//					File file = new File("C:\\Users\\Michael\\Pictures\\im_super_serial__2.png");
//					System.out.println("Datei gefunden, Laenge = " + file.length());
//					ByteBuffer bbu = ByteBuffer.allocate((int) file.length());
//					System.out.println("ByteBuffer erzeugt");
//					final int BYTES_PER_READ = (int) file.length();
//					System.out.println("Bytes ermittelt");
//				    FileInputStream fis = new FileInputStream("C:\\Users\\Michael\\Pictures\\im_super_serial__2.png");
//				    System.out.println("Inputstream erstellt");
//				    int bytesRead = 0;
//				    byte[] buf = new byte[BYTES_PER_READ];
//				    System.out.println("Buf erzeugt");
//				    while (bytesRead != -1)
//				    {
//				        bbu.put(buf, 0, bytesRead);
//				        bytesRead = fis.read(buf);
//				        System.out.println(" " + bytesRead);
//				    }
//				    System.out.println("Bytes kopiert");
//				    fis.close();
//				    
//					BufferedImage image = ImageIO.read( file);
//					B = image.getWidth();
//					H = image.getHeight();
					/* Test */
					/* Work */
					BufferedImage image = CarControllerWrapper.getImg();
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
			        System.out.println("Bildgr��e bestimmt und RGB- Image erzeugt");
			        
			        System.out.println("Daten f�r ByteBuffer: " + B * H);
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
			        System.out.println("Bildh�he in ByteBuffer");
			        
			        
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
			        	        
					while (iter.hasNext()) {
						someClient = (MessageInbound) iter.next();
						try {
							System.out.println("ByteBuffer senden");
							//someClient.getWsOutbound().writeBinaryMessage( ByteBuffer.wrap(buf));
							someClient.getWsOutbound().writeBinaryMessage( buffer);
							System.out.println("ByteBuffer gesendet");
						} catch (IOException e) {
						}
					}
					
					
				}

				@Override
				protected void onTextMessage(CharBuffer cb) throws IOException {			
					String msg = cb.toString();
					
					System.out.println("onTextMessage: " + msg);
					/**
					 * Controllerpart
					 */
					/*
					if(msg.startsWith("C%:")) {
						char key = msg.charAt(msg.indexOf(":") + 1);
						{
							switch (key) {
							case 'a':
								System.out.println("driveLeft");
								CarControllerWrapper.driveLeft();
								break;
							case 'd':
								System.out.println("driveRight");
								CarControllerWrapper.driveRight();
								break;
							case 'w':
								System.out.println("driveForward");
								CarControllerWrapper.driveForward();
								break;
							case 's':
								System.out.println("driveBackward");
								CarControllerWrapper.driveBackward();
								break;
							case 'h':
								System.out.println("honk");
								///TODO \todo honk
								break;
							case 'l':
								System.out.println("light");
								///TODO \todo light
								break;
		
							}
						}
					}
					/**
					 * Chatpart
					 */
					/*
					else {					
						String nickName = (String)session.getAttribute("nickName");
						
						System.out.println(nickName);
						
						String userId = (String)session.getAttribute("userId");
						int sessionId = (int)session.getAttribute("dbSessionID");
						log.logChat(userId, sessionId, cb.toString());
						
						// Send message to all clients connected
						broadcast(nickName + ": " + msg);
					}
					*/
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
					someClient.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
					
				} catch (IOException e) {
				}
			}
		}
}




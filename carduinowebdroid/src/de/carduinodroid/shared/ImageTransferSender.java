package de.carduinodroid.shared;
 
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import de.carduinodroid.utilities.CarControllerWrapper;
 
/**
 * Dieses Package dient dem Senden von Bilddaten über das Internet von einem Server zu einem Client
 * @author Vincenz
 * @version 15.06.2012
 */ 
 
class Util {
 
    public static final void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
 
class FrameProducer implements Runnable {
 
    static final int W = 640;
    static final int H = 480;
    static final int D = 30;
    //private final BufferedImage buffer = new BufferedImage(W, H, BufferedImage.TYPE_3BYTE_BGR);
    //private final BufferedImage buffer = new BufferedImage(W, H, BufferedImage.TYPE_4BYTE_ABGR);
    private final BufferedImage buffer = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
    private final Image background;
    private static final Logger logger = Logger.getLogger(FrameProducer.class.getName());
    private final ImageWriter imageWriter;
   
    public FrameProducer() {
        Image img = null;
        ImageWriter wr = null;
		// Bilddatei für Testzwecke mit Abmaßen >= 640 x 480 pix
        //Test File file = new File("H:\\Eclipse-Apps\\Sender\\GutscheinHinten.jpg");
        try {
            //Hier jpg Bilder laden
            
        	//Test img = ImageIO.read(file);
			img = CarControllerWrapper.getImg();
            wr = ImageIO.getImageWritersByFormatName("JPEG").next();
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
        } finally {
            background = img;
            imageWriter = wr;
        }
    }
 
    public void run() {
 
        logger.info("producing frames...");
        int x = D, y = D, inc_x = 1, inc_y = 1, ix = 50, iy = 05, inc_ix = 1, inc_iy = 1;
 
        final Font font = new Font("Arial", Font.BOLD, 21);
 
        final Graphics2D g = buffer.createGraphics();
        g.setFont(font);
 
        final int backgroundW = background.getWidth(null) ;
        final int backgroundH = background.getHeight(null);
        
        int nFrames = 0;
        long start = System.currentTimeMillis();
 
        for (;;) {
 
            synchronized (buffer) {
 
                g.drawImage(background, 0, 0, W, H, ix, iy, ix + W, iy + H, null);
                if (ix > backgroundW - W || ix < 0) {
                    inc_ix *= -1;
                }
                if (iy > backgroundH - H || iy < 0) {
                    inc_iy *= -1;
                }
                ix += inc_ix;
                iy += inc_iy;
                g.setColor(Color.WHITE);
                g.fillRect(x, y, D, D);
                if (x > W - D || x < 0) {
                    inc_x *= -1;
                }
                if (y > H - D || y < 0) {
                    inc_y *= -1;
                }
                x += inc_x;
                y += inc_y;
                g.setColor(Color.GREEN);
                final String current = System.currentTimeMillis() + "";
                g.drawString(current, 20, 20);
            }
 
            Util.sleep(10);
 
            nFrames++;
            final long now = System.currentTimeMillis();
            final long delta = now - start;
            if (delta > 2000L) {
                float fps = ((float) nFrames) / delta * 1000.0f;
                logger.info(String.format("%1$2.3fFPS", fps));
 
                start = System.currentTimeMillis();
                nFrames = 0;
            }
        }
    }
 
    public RenderedImage grabFrame() {
        final BufferedImage frame;
        synchronized (buffer) {
            frame = new BufferedImage(buffer.getWidth(),
                    buffer.getHeight(), buffer.getType());
            frame.setData(buffer.getData());
        }
        return frame;
    }
 
    public byte[] grabJPEGFrame() throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(grabFrame(), "jpeg", bos);
        return bos.toByteArray();
    }
}
 
abstract class Server implements Runnable {
 
    private final int port;
    private static final ExecutorService EXS = Executors.newCachedThreadPool();
    private static final Logger logger = Logger.getLogger(Server.class.getName());
 
    public Server(int port) {
        this.port = port;
    }
 
    abstract Object doServe(final Socket socket) throws Exception;
    
    public void run() {
        ServerSocket ssock = null;
        try {
            try {
                ssock = new ServerSocket(port);
 
                for (;;) {
                    final Socket sock = ssock.accept();
                    // Lauscht auf Port und verschickt Pakete an die anfragende Adresse
                    logger.info("accept: " + sock.getInetAddress());
                    EXS.submit(new Callable<Object>() {
 
                        public Object call() throws Exception {
                            return doServe(sock);
                        }
                    });
                }
            } finally {
                if (ssock != null) {
                    ssock.close();
                }
            }
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
 
class MJPGServer extends Server {
 
    private static final String boundary = "myboundary";
    private static final byte[] boundaryBytes = ("\r\n--" + boundary + "\r\n").getBytes();
    private static final byte[] contentTypeImageJPEG = "Content-Type: image/jpeg\r\n".getBytes();
    private static final Logger logger = Logger.getLogger(MJPGServer.class.getName());
    //private static final ExecutorService EXS = Executors.newCachedThreadPool();
    private final FrameProducer frameProducer;
 
    public MJPGServer(final FrameProducer frameProducer, int port) {
        super(port);
        this.frameProducer = frameProducer;
    }
 
    @Override
    Object doServe(Socket socket) throws Exception {
        final OutputStream os = socket.getOutputStream();
 
        os.write(("HTTP/1.1 200 OK\r\n"
                + "Expires: 0\r\n"
                + "Pragma: no-cache\r\n"
                + "Cache-Control: no-cache\r\n"
                + "Content-Type: multipart/x-mixed-replace;boundary=" + boundary + "\r\n").getBytes());
 
        for (;;) {
            try {
                os.write(boundaryBytes);
                os.write(contentTypeImageJPEG);
 
                final byte[] jpg = frameProducer.grabJPEGFrame();
                os.write(("Content-Length: " + jpg.length + "\r\n\r\n").getBytes());
                os.write(jpg);
                os.flush();
            } catch (final Exception ex) {
                logger.severe(ex.getMessage());
                break;
            }
            Util.sleep(25);
        }
        logger.info("exit: " + socket.getInetAddress());
        return null;
    }
}
 
class RawFrameServer extends Server {
 
    private final FrameProducer frameProducer;
 
    public RawFrameServer(final FrameProducer frameProducer, int port) {
        super(port);
        this.frameProducer = frameProducer;
    }
 
    @Override
    Object doServe(Socket socket) throws Exception {
        final OutputStream os = socket.getOutputStream();
        final byte buffer [] = new byte [4];
        for(;;) {
            final byte [] frame = frameProducer.grabJPEGFrame();
 
            final int l = frame.length;
            buffer[0] = (byte) (l & 0xff);
            buffer[1] = (byte) ((l >> 8) & 0xff);
            buffer[2] = (byte) ((l >> 16) & 0xff);
            buffer[3] = (byte) ((l >> 24) & 0xff);
 
            os.write(buffer ,0 ,4);
            os.write(frame);
            os.flush();
        }
    }
}
 
 public class ImageTransferSender {
/**
 * Diese Klasse dient dem Senden von Bilddaten über das Internet von einem Server zu einem Client
 * @author Vincenz
 * @version 15.06.2012
 */
    private static final Logger logger = Logger.getLogger(ImageTransferSender.class.getName());
    // FrameProducer stellt die Bilder, die Gesendet werden zur Verfügung --> Testzwecke
    static final FrameProducer frameProducer = new FrameProducer();
    // MJPGServer Sendet die Bilder von FrameProducer an Port 8889
    // kann in Firefox direkt aufgerufen werden
    static final MJPGServer MJPGServer = new MJPGServer(frameProducer, 8889);
    // RawFrameServer sorgt dafür, daß der Buffer mit dem richtigen Frame gefüllt wird
    // Kontrollieren kann man den Datenstrom an Port 8888
    static final RawFrameServer rawFrameServer = new RawFrameServer(frameProducer, 8888);
    // EXS ist die Javainterne Threadverwaltung
    static final ExecutorService EXS = Executors.newCachedThreadPool();
 
    public static void main(String[] args) throws IOException, InterruptedException {
 
        EXS.submit(frameProducer);
        EXS.submit(MJPGServer);
        EXS.submit(rawFrameServer);
    }
}

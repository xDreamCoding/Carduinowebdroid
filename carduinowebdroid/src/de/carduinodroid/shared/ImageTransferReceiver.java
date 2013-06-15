package de.carduinodroid.shared;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

// Empfänger
public class ImageTransferReceiver extends JFrame implements Runnable {

    final JPanel picPanel = new JPanel();
    private static final Logger logger = Logger.getLogger(ImageTransferReceiver.class.getName());
 
    public ImageTransferReceiver() {
        final Container cnt = getContentPane();
        cnt.setLayout(new BorderLayout());
 
        picPanel.setBackground(Color.GREEN);
        cnt.add(picPanel, BorderLayout.CENTER);
    }
 
    public void readBytes(final InputStream is, final byte[] buffer, int l) throws IOException {
        int left = l;
        int read = 0;
 
        while (left > 0) {
            int n = is.read(buffer, read, left);
            left -= n;
            read += n;
        }
    }
 
    public void run() {
        try {
        	// Server IP und Port angeben
            final Socket sock = new Socket("192.168.26.23", 8888);
            final InputStream is = sock.getInputStream();
            final byte[] buffer = new byte[4];
 
            int nFrames = 0;
            int readBytes = 0;
            long start = System.currentTimeMillis();
 
            for (;;) {
                readBytes(is, buffer, 4);
                readBytes += 4;
 
                int size = 0;
                size += buffer[0] & 0xff;
                size += (buffer[1] << 8) & 0xff00;
                size += (buffer[2] << 16) & 0xff0000;
                size += (buffer[3] << 24) & 0xff000000;
 
                final byte[] jpeg_frame = new byte[size];
                readBytes(is, jpeg_frame, size);
 
                final Image img = ImageIO.read(new ByteArrayInputStream(jpeg_frame));
                picPanel.getGraphics().drawImage(img, 0, 0, null);
 
                nFrames++;
                readBytes += size;
                
                final long now = System.currentTimeMillis();
                final long delta = now - start;
                if (delta > 2000L) {
                    final float d = ((float)delta) / 1000.0f;
                    float fps = ((float) nFrames) / d;
                    logger.info(String.format("%1$2.3fFPS, %2$2.3f kB/sec", fps,
                    ((float)readBytes / d) / 1024.0f));
 
                    start = System.currentTimeMillis();
                    nFrames = 0;
                    readBytes=0;
                }
            }
        } catch (final Throwable t) {
        }
    }
 
    public static void main(final String[] args) {
		// Aufbau des Frames in der Website
        final ImageTransferReceiver c = new ImageTransferReceiver();
        c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        Executors.newSingleThreadExecutor().submit(c);
 
        c.setSize(800, 600);
        c.setVisible(true);
    }
}
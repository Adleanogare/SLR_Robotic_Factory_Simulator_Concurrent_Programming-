package fr.tp.inf112.projects.robotsim.server;

import java.util.Arrays;
import java.util.logging.Logger;
import java.net.*;
import java.nio.file.Paths;
import java.io.*;

import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;
import fr.tp.inf112.projects.robotsim.model.FactoryPersistenceManager;

public class SimulatorPersistenceServer {
	
	private static final Logger LOGGER = Logger.getLogger(SimulatorPersistenceServer.class.getName());
	private static final FileCanvasChooser canvasChooser = new FileCanvasChooser("factory", "Puck Factory1");
	private static final FactoryPersistenceManager fpm = new FactoryPersistenceManager(canvasChooser);
	
	public static Logger getLogger() {
		return LOGGER;
	}
	
	public static void main(String args[]) {
		LOGGER.info("Starting the webserver simulator...");
		
		LOGGER.config("With parameters " + Arrays.toString(args) + ".");
        try {
            InetAddress specificAddr = InetAddress.getByName("127.0.0.1");
            try (
                ServerSocket serverSocket = new ServerSocket(64000, 50, specificAddr);
            ) { do {
                try {
                    LOGGER.info(String.format("Server connected: %s%n", serverSocket.getInetAddress()));
                    while(true) {
                    	LOGGER.info("Waiting for a connexion...");
                        Socket socket = serverSocket.accept();
                        LOGGER.info(String.format("Client connected: %s%n", socket.getRemoteSocketAddress()));
                        new Thread(() -> runWebserver(socket)).start();
                    }
                }
                catch (IOException ex) {
                	LOGGER.severe("Error while logging");
                	LOGGER.severe(ex.toString());
                }
            } while (true); }
            catch (IOException ex) {
            	LOGGER.severe("Error while logging");
            	LOGGER.severe(ex.toString());
            }
        }
        catch (UnknownHostException ex) {
        	LOGGER.severe("UnknownHostException");
        	LOGGER.severe(ex.toString());
        }
    }

	public static void runWebserver(Socket socket) {
		LOGGER.info(socket.getRemoteSocketAddress().toString());
		if (socket.isInputShutdown()) {
			LOGGER.severe("Input Server shutdown");
		}
		if (socket.isOutputShutdown()) {
			LOGGER.severe("Output Server shutdown");
		}
        try (
            InputStream inpStr = socket.getInputStream();
            BufferedInputStream bfin = new BufferedInputStream(inpStr);
            ObjectInputStream in = new ObjectInputStream(bfin);
            // - read and decode input request
            OutputStream outStr = socket.getOutputStream();
            BufferedOutputStream bfout = new BufferedOutputStream(outStr);
            ObjectOutputStream out = new ObjectOutputStream(bfout);
            // - build and write response
        	) /*while(true) {*/ {
        		LOGGER.info("Waiting for a message");
        		//LOGGER.info("Will read !!!");
        		Object o = in.readObject();
        		//LOGGER.info("Received object successfully !!!");
        		if (o instanceof String) {
        			LOGGER.info("I received : " + o.toString() + " !");
        			fpm.read(o.toString());
        			//out.writeObject("I received : " + o.toString() + " !");
        		} else if (o instanceof Canvas) {
        			LOGGER.info("I received : a Canvas !!!");
        			fpm.persist((Canvas) o);
        			LOGGER.info("Canvas was saved successfully !!!");
        		} else if (o instanceof FilenameFilter) {
        			LOGGER.info("I received : a file extension !!!");
        		    File file = new File(Paths.get(".").toString());
        			out.writeObject(file.list((FilenameFilter) o));
        		} else {
        			LOGGER.severe("No class found");
        		}            		
        }
    	catch (EOFException ex) {
    		LOGGER.severe("EOF exception");
    		LOGGER.severe(ex.toString());
    	}
    	catch (IOException ex) {
    		LOGGER.severe("Error request server : writing object ");
        	LOGGER.severe(ex.toString());
    	}
    	catch (ClassNotFoundException ex) {
    		LOGGER.severe("Error request server : class not found");
        	LOGGER.severe(ex.toString());
    	}
    	/*finally {
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOGGER.severe("Issue while sleeping");
			}
    	} //}
    	*/
    }
}

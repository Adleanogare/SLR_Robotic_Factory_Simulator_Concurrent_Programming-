package fr.tp.inf112.projects.robotsim.server;

import java.util.Arrays;
import java.util.logging.Logger;
import java.net.*;
import java.nio.file.Paths;
import java.io.*;
import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.model.FactoryPersistenceManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SimulatorPersistenceServer {
	
	private static final Logger LOGGER = Logger.getLogger(SimulatorPersistenceServer.class.getName());
	
	private static final FactoryPersistenceManager fpm = new FactoryPersistenceManager(null);
	
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
                    LOGGER.fine(String.format("Client connected: %s%n", serverSocket.getInetAddress()));
                    while(true) {
                    	LOGGER.fine("Waiting for a connexion...");
                        Socket socket = serverSocket.accept();
                        LOGGER.fine(String.format("Client connected: %s%n", socket.getRemoteSocketAddress()));
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
        try {
            LOGGER.fine(socket.getRemoteSocketAddress().toString());
            InputStream inpStr = socket.getInputStream();
            BufferedInputStream bfin = new BufferedInputStream(inpStr);
            ObjectInputStream in = new ObjectInputStream(bfin);

            // - read and decode input request
            OutputStream outStr = socket.getOutputStream();
            BufferedOutputStream bfout = new BufferedOutputStream(outStr);
            ObjectOutputStream out = new ObjectOutputStream(bfout);
            // - build and write response
            String s = null;
            LOGGER.fine("Waiting for a message");
            while(true) {
            	try {
            		Object o = in.readObject();
            		if (o instanceof String) {
            			fpm.read(o.toString());
            			out.writeObject("I received : " + o.toString() + " !");
            			LOGGER.fine("I received : " + o.toString() + " !");
            		} else if (o instanceof Factory) {
            			fpm.persist((Factory) o);
            			LOGGER.fine("I received : a Factory !!!");
            		} else if (o instanceof FileNameExtensionFilter) {
            			java.io.FileFilter ioFilter = file -> ((FileNameExtensionFilter) o).accept(file);
            		    File file = new File(Paths.get("").toString());
            			out.writeObject(file.listFiles(ioFilter));
            		}
            	}
            	catch (IOException | ClassNotFoundException ex) {out.writeObject("I received : " + s + " !");
            		LOGGER.severe("Error request server");
                	LOGGER.severe(ex.toString());
            	}
            }
        }
        catch (IOException ex) {
        	LOGGER.severe("Error request server");
        	LOGGER.severe(ex.toString());
        }
    }
}

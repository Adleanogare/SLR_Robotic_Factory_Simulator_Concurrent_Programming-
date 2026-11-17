package fr.tp.inf112.projects.robotsim.server;

import java.util.Arrays;
import java.util.logging.Logger;
import java.net.*;
import java.io.*;


public class SimulatorPersistenceServer {
	
	private static final Logger LOGGER = Logger.getLogger(SimulatorPersistenceServer.class.getName());
	
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
            System.out.println(socket.getRemoteSocketAddress().toString());
            InputStream inpStr = socket.getInputStream();
            Reader strReader = new InputStreamReader(inpStr);
            BufferedReader buffReader = new BufferedReader(strReader);

            // - read and decode input request
            OutputStream outStr = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outStr, true); // Autoflush
            // - build and write response
            String s = null;
            System.out.println("Waiting for a message");
            while(true) {
                while ((s=buffReader.readLine())!=null) {
                    writer.println("I received : " + s + " !");
                    System.out.println("I received : " + s + " !");
                }
            }
            
        }
        catch (IOException ex) {
        	System.out.println("Erreur request serveur");
        	System.out.println(ex);
        }
    }
}

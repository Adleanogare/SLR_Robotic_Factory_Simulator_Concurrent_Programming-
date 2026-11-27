package fr.tp.inf112.projects.robotsim.server;

import java.util.Arrays;
import java.util.logging.Logger;
import java.net.*;
import java.io.*;


public class SimulatorPersistenceClient {
	
	private static final Logger LOGGER = Logger.getLogger(SimulatorPersistenceClient.class.getName());
	
	public static Logger getLogger() {
		return LOGGER;
	}
	
	public static void main(String args[]) {
		LOGGER.info("Starting the client simulator...");
		
		LOGGER.config("With parameters " + Arrays.toString(args) + ".");
		
		try { 
            InetAddress netAddr = InetAddress.getByName("127.0.0.1");
            try {
        		LOGGER.fine("TryInet");
                SocketAddress sockAddr = new InetSocketAddress(netAddr, 64000);
                LOGGER.fine("TrySocket");
                Socket socket = new Socket();
                socket.connect(sockAddr, 1000); // Timeout of 1000 ms
                LOGGER.fine("RunClient");
                new Thread(() -> runClient(socket)).start();
            }
            catch (IOException ex) {
            	LOGGER.severe("Error");
                LOGGER.severe(ex.toString());
            }
        }
        catch (IOException ex) {
        	LOGGER.severe("Connection Error");
            LOGGER.severe(ex.toString());
        }
    }

	public static void runClient(Socket socket) {
        try {
        	LOGGER.fine(socket.getRemoteSocketAddress().toString());
            InputStream inpStr = socket.getInputStream();
            Reader strReader = new InputStreamReader(inpStr);
            BufferedReader buffReader = new BufferedReader(strReader);

            // - read and decode input request
            OutputStream outStr = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outStr, true); // Autoflush
            // - build and write response
            String s = null;
            writer.println("I send HelloWorld!");
            LOGGER.fine("I send HelloWorld!");
            while ((s=buffReader.readLine())!=null) {
                LOGGER.fine("I received back : " + s + " !");
            }
            
        }
        catch (IOException ex) {
        	LOGGER.severe("Client error");
            LOGGER.severe(ex.toString());
        }
    }
}

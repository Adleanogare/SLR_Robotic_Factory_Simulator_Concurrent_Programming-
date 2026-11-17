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
                System.out.println("TryInet");
                SocketAddress sockAddr = new InetSocketAddress(netAddr, 64000);
                System.out.println("TrySocket");
                Socket socket = new Socket();
                socket.connect(sockAddr, 1000); // Timeout of 1000 ms
                System.out.println("Processor");
                new Thread(() -> runClient(socket)).start();
            }
            catch (IOException ex) {
                System.out.println("Erreur");
                System.out.println(ex);
            }
        }
        catch (IOException ex) {
            System.out.println("Erreur connection");
        }
    }

	public static void runClient(Socket socket) {
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
            writer.println("I send HelloWorld!");
            System.out.println("I send HelloWorld!");
            while ((s=buffReader.readLine())!=null) {
                System.out.println("I received back : " + s + " !");
            }
            
        }
        catch (IOException ex) {
        	System.out.println("Error request client");
        	System.out.println(ex);
        }
    }
}

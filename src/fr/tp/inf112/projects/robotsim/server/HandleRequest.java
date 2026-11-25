package fr.tp.inf112.projects.robotsim.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Logger;

public class HandleRequest {

	private static final int CONNECT_TIMEOUT_MILLIS = 1000;
	
	private final Logger LOGGER;
	
	private final SocketAddress sockAddr;
	
	public HandleRequest(SocketAddress sockAddr, Logger LOGGER) {
		this.sockAddr = sockAddr;
		this.LOGGER = LOGGER;
	}
	
	public Object request(Object input) throws IOException {
		
		Logger LOGGER = this.LOGGER;
		
		try (Socket socket = new Socket()) {
			LOGGER.info("Request - socket connected");
			socket.connect(sockAddr, CONNECT_TIMEOUT_MILLIS);
			socket.setSoTimeout(CONNECT_TIMEOUT_MILLIS);
			LOGGER.info(socket.getRemoteSocketAddress().toString());
			
			try (
				final OutputStream outStr = socket.getOutputStream();
				final BufferedOutputStream bfout = new BufferedOutputStream(outStr);
				final ObjectOutputStream out = new ObjectOutputStream(bfout);		
				) {
				out.flush();
				LOGGER.info("Request - streams up");
				out.writeObject(input);
				out.flush();
				LOGGER.info("Request - object written");
				try (
			        final InputStream inpStr = socket.getInputStream();
					final BufferedInputStream bfin = new BufferedInputStream(inpStr);
					final ObjectInputStream in = new ObjectInputStream(bfin);		
					) {
					return in.readObject();
				} catch (IOException ex) {
					LOGGER.severe("Request - Exception while read");
					LOGGER.severe(ex.toString());
				}
			} catch (IOException ex) {
				LOGGER.severe("Request - Exception while write");
				LOGGER.severe(ex.toString());
			} catch (ClassNotFoundException ex) {
				LOGGER.severe("Request - Exception reading a bad class");
				LOGGER.severe(ex.toString());
			}
			
		} catch (IOException ex) {
			LOGGER.severe("Request - Exception while connecting to the socket");
			LOGGER.severe(ex.toString());
		}
		
		throw new IOException("The request failed");
	}
	
}

package fr.tp.inf112.projects.robotsim.server;

import java.util.logging.Logger;

import java.net.*;
import java.io.*;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.CanvasChooser;
import fr.tp.inf112.projects.canvas.model.impl.AbstractCanvasPersistenceManager;

public class RemoteFactoryPersistenceManager extends AbstractCanvasPersistenceManager {
	
	private final Socket serverSocket;
	
	public RemoteFactoryPersistenceManager(final CanvasChooser canvasChooser) {
		super(canvasChooser);
		serverSocket = null;
	}
	
	public RemoteFactoryPersistenceManager(final CanvasChooser canvasChooser, final Socket socket) {
		super(canvasChooser);
		serverSocket = socket;
	}
	
	private static final Logger LOGGER = Logger.getLogger(RemoteFactoryPersistenceManager.class.getName());
	
	public static Logger getLogger() {
		return LOGGER;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Canvas read(final String canvasId)
	throws IOException {
		try (
	        final InputStream inpStr = serverSocket.getInputStream();
			final BufferedInputStream bfin = new BufferedInputStream(inpStr);
			final ObjectInputStream in = new ObjectInputStream(bfin);
			//final InputStream fileInputStream = new FileInputStream(canvasId);
		) {
			return (Canvas) in.readObject();
		}
		catch (ClassNotFoundException | IOException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void persist(Canvas canvasModel)
	throws IOException {
		try (
	        final OutputStream outStr = serverSocket.getOutputStream();
	        final BufferedOutputStream bfout = new BufferedOutputStream(outStr);
	        final ObjectOutputStream out = new ObjectOutputStream(bfout);	
			//final OutputStream fileOutStream = new FileOutputStream(canvasModel.getId());
		) {	
			out.writeObject(canvasModel);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean delete(final Canvas canvasModel)
	throws IOException {
		return false; // Should never be called
	}
	
}

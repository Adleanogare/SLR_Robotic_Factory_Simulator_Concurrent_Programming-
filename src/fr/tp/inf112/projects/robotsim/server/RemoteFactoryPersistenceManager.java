package fr.tp.inf112.projects.robotsim.server;


import java.net.*;
import java.util.logging.Logger;
import java.io.*;
import fr.tp.inf112.projects.canvas.model.Canvas;
import fr.tp.inf112.projects.canvas.model.impl.AbstractCanvasPersistenceManager;
import fr.tp.inf112.projects.robotsim.app.SimulatorApplication;


public class RemoteFactoryPersistenceManager extends AbstractCanvasPersistenceManager {
	
	private final SocketAddress serverSocketAddr;
	
	final Logger LOGGER = SimulatorApplication.getLogger();
		
	public RemoteFactoryPersistenceManager(final RemoteFileCanvasChooser canvasChooser) {
		super(canvasChooser);
		serverSocketAddr = canvasChooser.getSocketAddr();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Canvas read(final String canvasId)
	throws IOException {
		LOGGER.info("Canvas Read");
		return (Canvas) new HandleRequest(serverSocketAddr, LOGGER).request(canvasId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void persist(Canvas canvasModel)
	throws IOException {
		LOGGER.info("Canvas Persist");
		if ((boolean) new HandleRequest(serverSocketAddr, LOGGER).request(canvasModel)) {
			LOGGER.info("Canvas Persist Successfull");
		} else {
			LOGGER.info("Canvas Persist Failed");
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

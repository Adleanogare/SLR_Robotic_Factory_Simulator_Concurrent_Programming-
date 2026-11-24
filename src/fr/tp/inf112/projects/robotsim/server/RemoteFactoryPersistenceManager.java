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
		try (Socket serverSocket = new Socket();) {
			serverSocket.connect(serverSocketAddr, 1000);
			LOGGER.info("RFPM - read - socket connected");
			try (
		        final InputStream inpStr = serverSocket.getInputStream();
				final BufferedInputStream bfin = new BufferedInputStream(inpStr);
				final ObjectInputStream in = new ObjectInputStream(bfin);
				//final InputStream fileInputStream = new FileInputStream(canvasId);
			) {
				LOGGER.info("RFPM - read - stream connected");
				return (Canvas) in.readObject();
			}
			catch (ClassNotFoundException | IOException ex) {
				LOGGER.info("RFPM - read - failS");
				throw new IOException(ex);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void persist(Canvas canvasModel)
	throws IOException {
		try (Socket serverSocket = new Socket();) {
			serverSocket.connect(serverSocketAddr, 1000);
			LOGGER.info("RFPM - persist - socket connected");
			try (
				final OutputStream outStr = serverSocket.getOutputStream();
			    final BufferedOutputStream bfout = new BufferedOutputStream(outStr);
			    final ObjectOutputStream out = new ObjectOutputStream(bfout);	
				) {	
				//final OutputStream fileOutStream = new FileOutputStream(canvasModel.getId());
				out.writeObject(canvasModel);
				LOGGER.info("RFPM - persist - object written in stream");
			} 
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

package fr.tp.inf112.projects.robotsim.server;


import java.awt.Component;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;
import fr.tp.inf112.projects.robotsim.app.SimulatorApplication;


public class RemoteFileCanvasChooser extends FileCanvasChooser {
	
	final Logger LOGGER = SimulatorApplication.getLogger();
	
	private final SocketAddress serverSocketAddr;
	
	/**
	 * The UI component that will serve as the parent of the {@code JFileChooser} displayed to the user. This is typically
	 * a Java AWT component such as a {@code CanvasViewer} object. 
	 */
	private Component viewer;
	
    /**
     * Used by the {@code JFileChooser} to filter the files presented to the user according to specific file extensions.
     */
    //private final FilenameFilter fileNameFilter;
	private final Function<String, Boolean> fileNameFilter;
	
    @SuppressWarnings("unchecked")
	public RemoteFileCanvasChooser(final String fileExtension, final String documentTypeLabel, final SocketAddress socketAddr) {
    	super(null, fileExtension, documentTypeLabel);
		fileNameFilter = (Function<String, Boolean>  & Serializable) name -> name.toLowerCase().endsWith("."+fileExtension);
		serverSocketAddr = socketAddr;
    }
    
    @SuppressWarnings("unchecked")
	public RemoteFileCanvasChooser(final Component viewer, final String fileExtension, final String documentTypeLabel, final SocketAddress socketAddr) {
    	super(null, fileExtension, documentTypeLabel);
		fileNameFilter = (Function<String, Boolean>  & Serializable) name -> name.toLowerCase().endsWith("."+fileExtension);
		serverSocketAddr = socketAddr;
		this.viewer = viewer;
    }
    
	public Component getViewer() {
		return this.viewer;
	}
	
	public void setViewer(Component viewer) {
		this.viewer = viewer;
	}

    public SocketAddress getSocketAddr() {
    	return this.serverSocketAddr;
    }
    
    public String[] getFactories() {
    	try {
			return (String[]) new HandleRequest(serverSocketAddr, LOGGER).request(fileNameFilter);
		} catch (IOException e) {
			LOGGER.severe("Failed Request while asking for a list of factories.");
			LOGGER.severe(e.toString());
		}
		return null;
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String browseCanvases(final boolean open) // this is client side
	throws IOException {
	    if (open) {
	    	LOGGER.info("BrowseCanvas is open");
	    	String[] selectionValues = getFactories();
	    	LOGGER.info(Arrays.toString(selectionValues));
		    return JOptionPane.showInputDialog(null, "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null, selectionValues, selectionValues[0]).toString();

	    }
	    else {
	    	LOGGER.info("BrowseCanvas isn't open");
	    	final String res = JOptionPane.showInputDialog(getViewer(), "Input : Filename of the factory model ", "Save Factory", JOptionPane.QUESTION_MESSAGE);
	    	LOGGER.info(res);
	    	return res.trim();
	    }
	}
}

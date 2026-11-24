package fr.tp.inf112.projects.robotsim.server;


import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.*;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;
import fr.tp.inf112.projects.robotsim.app.SimulatorApplication;
//import fr.tp.inf112.projects.robotsim.model.Component;

public class RemoteFileCanvasChooser extends FileCanvasChooser {
	
	final Logger LOGGER = SimulatorApplication.getLogger();
	
	//Socket of the persistence server
	private final SocketAddress serverSocketAddr;
	
	/**
	 * The UI component that will serve as the parent of the {@code JFileChooser} displayed to the user. This is typically
	 * a Java AWT component such as a {@code CanvasViewer} object. 
	 */
	//private Component viewer;
	
    /**
     * Used by the {@code JFileChooser} to filter the files presented to the user according to specific file extensions.
     */
    private final FilenameFilter fileNameFilter;
	
    public RemoteFileCanvasChooser(final String fileExtension, final String documentTypeLabel, final SocketAddress socketAddr) {
    	super(null, fileExtension, documentTypeLabel);
		fileNameFilter = (dir,name) -> name.toLowerCase().endsWith("."+fileExtension);
		serverSocketAddr = socketAddr;
    }

    public SocketAddress getSocketAddr() {
    	return this.serverSocketAddr;
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String browseCanvases(final boolean open) // this is client side
	throws IOException {
	    if (open) {
	    	/* a list of model files should be displayed to the user so that he can choose which model
	    	to open. For this, ask the persistence server for a list of existing model file names on its file
	    	system. For simplicity, you can only return the files that are directly located on the working
	    	directory of the server, without searching for files in subdirectories.
	    	Next, display the list of files on the server by using the JOPtionPane class of the JDK. For this,
	    	call its static showInputDialog() method and provide the array of file names received from
	    	the server to its selectionValues parameter. The opened dialog box will automatically display
	    	a combo box containing the list of files, and the user can select which file he wants to open
	    	from the combo’s drop-down menu. */
	    	try (Socket serverSocket = new Socket();) {
	    		serverSocket.connect(serverSocketAddr, 1000);
				LOGGER.info("RFCC - browse - socket connected");
				LOGGER.info(serverSocket.getRemoteSocketAddress().toString());

				try (
			    	final InputStream inpStr = serverSocket.getInputStream();
		            final BufferedInputStream bfin = new BufferedInputStream(inpStr);
		            final ObjectInputStream in = new ObjectInputStream(bfin);
		
		            // - read and decode input request
		            final OutputStream outStr = serverSocket.getOutputStream();
		            final BufferedOutputStream bfout = new BufferedOutputStream(outStr);
		            final ObjectOutputStream out = new ObjectOutputStream(bfout);
					) {
		            // - build and write response
		            LOGGER.info("RFCC - browse - gave filter");
		            if (!serverSocket.isInputShutdown()) {
		            	throw new IOException();
		            }
		            if (serverSocket.isInputShutdown()) {
		            	throw new IOException();
		            }
		            out.writeObject(fileNameFilter);
		            LOGGER.info("RFCC - browse - gave filter");
		            
		            String[] selectionValues = null;
		            
			    	try {
			    		selectionValues = (String[]) in.readObject();
			    		LOGGER.info("RFCC - browse - readObject");
			    	} catch (ClassNotFoundException ex) {
		        		LOGGER.severe("Error request server for a file selection value");
		            	LOGGER.severe(ex.toString());
			    	}
			    	JOptionPane.showMessageDialog(null, "Hello World!", "Title", JOptionPane.INFORMATION_MESSAGE);   
		    		return JOptionPane.showInputDialog(null, "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null, selectionValues, selectionValues[0]).toString();
		    	}
			}
	    }
	    else {
	    	/* it means that the user should be prompted to provide a file
	    	name for the model. In such case, again use the JOptionPane class of the JDK to simply open a
	    	dialog box with only one input text field for the user to enter the name of the file. */
	    	LOGGER.info("browse isn't open");
	    	return JOptionPane.showInputDialog("Input : Filename of the factory model ");
	    }
	}
}

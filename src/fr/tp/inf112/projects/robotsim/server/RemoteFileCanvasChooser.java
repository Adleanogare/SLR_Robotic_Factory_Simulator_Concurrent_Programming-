package fr.tp.inf112.projects.robotsim.server;


import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.*;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import fr.tp.inf112.projects.canvas.view.FileCanvasChooser;

public class RemoteFileCanvasChooser extends FileCanvasChooser {
	
	private static final Logger LOGGER = Logger.getLogger(RemoteFileCanvasChooser.class.getName());
	
	//Socket of the persistence server
	private final Socket serverSocket;
	
	/**
	 * The UI component that will serve as the parent of the {@code JFileChooser} displayed to the user. This is typically
	 * a Java AWT component such as a {@code CanvasViewer} object. 
	 */
	//private Component viewer;
	
    /**
     * Used by the {@code JFileChooser} to filter the files presented to the user according to specific file extensions.
     */
    private final FileNameExtensionFilter fileNameFilter;
	
    public RemoteFileCanvasChooser(final String fileExtension, final String documentTypeLabel, final Socket socket) {
    	super(null, fileExtension, documentTypeLabel);
		fileNameFilter = new FileNameExtensionFilter(documentTypeLabel + " files " + "(*." + fileExtension + ")", fileExtension);
		serverSocket = socket;
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
	    	
	    	
	    	InputStream inpStr = serverSocket.getInputStream();
            BufferedInputStream bfin = new BufferedInputStream(inpStr);
            ObjectInputStream in = new ObjectInputStream(bfin);

            // - read and decode input request
            OutputStream outStr = serverSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(outStr, true); // Autoflush
            // - build and write response
            
            writer.println(fileNameFilter);
            
            File[] selectionValues = null;
            
	    	try {
	    		selectionValues = (File[]) in.readObject();
	    	} catch (ClassNotFoundException ex) {
        		LOGGER.severe("Error request server for a file selection value");
            	LOGGER.severe(ex.toString());
	    	}
	    	

	    	return JOptionPane.showInputDialog(null, "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null, selectionValues, selectionValues[0]).toString();
	    	
	    }
	    else {
	    	/* it means that the user should be prompted to provide a file
	    	name for the model. In such case, again use the JOptionPane class of the JDK to simply open a
	    	dialog box with only one input text field for the user to enter the name of the file. */
	    	return JOptionPane.showInputDialog("Input : Filename of the factory model ");
	    }
	}
}

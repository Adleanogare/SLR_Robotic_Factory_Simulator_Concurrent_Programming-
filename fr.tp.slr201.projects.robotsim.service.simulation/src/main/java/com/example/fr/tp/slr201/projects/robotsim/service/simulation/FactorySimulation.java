package com.example.fr.tp.slr201.projects.robotsim.service.simulation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.tp.inf112.projects.robotsim.model.Factory;
import fr.tp.inf112.projects.robotsim.server.RemoteFactoryPersistenceManager;
import fr.tp.inf112.projects.robotsim.server.RemoteFileCanvasChooser;


@RestController
@RequestMapping("/factory")
public class FactorySimulation {
	
	private static final Logger LOGGER = Logger.getLogger(FactorySimulation.class.getName());
	
	private RemoteFileCanvasChooser remoteFileCanvasChooser;
	private RemoteFactoryPersistenceManager remoteFactoryPersistenceManager;
	
    private final Map<String, Factory> factoriesSimulated = new LinkedHashMap<>();

	
	public FactorySimulation() {
		
		try { 
            InetAddress netAddr = InetAddress.getByName("127.0.0.1");
        	LOGGER.fine("TryInet");
            SocketAddress serverSocketAddr = new InetSocketAddress(netAddr, 64000);
            this.remoteFileCanvasChooser = new RemoteFileCanvasChooser("factory", "Puck Factory", serverSocketAddr);
            this.remoteFactoryPersistenceManager = new RemoteFactoryPersistenceManager(this.remoteFileCanvasChooser);
        } catch (IOException ex) {
        	LOGGER.severe("Connection Error to the inet");
            LOGGER.severe(ex.toString());
        }
	}
	
	@GetMapping("/search")
    public String search() {
		// Try curl http://localhost:8080/search
		LOGGER.info("Search factory from persistence manager.");
		String[] selectionValues = remoteFileCanvasChooser.getFactories();
		if (selectionValues != null) {
			String searchMenu = "";
			for (String s : selectionValues) {
				searchMenu = searchMenu.concat("- "+s+"\n");
			}
			return String.format("Factories :\n%s!", searchMenu);
		}
		return "No factory found";
	}
	
	@GetMapping("/running")
    public String running() {
		LOGGER.info("Search factory running from persistence manager.");
		// Try curl http://localhost:8080/running
		String[] selectionValues = remoteFileCanvasChooser.getFactories();
		if (selectionValues != null) {
			String searchMenu = "";
			for (String s : selectionValues) {
				if (factoriesSimulated.get(s) != null && factoriesSimulated.get(s).isSimulationStarted()) {
					searchMenu = searchMenu.concat("- "+s+" is running\n");
				} else {
					searchMenu = searchMenu.concat("- "+s+" is not running\n");
				}
				
			}
			return String.format("Factories running :\n%s!", searchMenu);
		}
		return "No factory found";
	}
	
	
    @GetMapping("/{factoryId}/start")
    public boolean startSimulation(@PathVariable("factoryId") final String factoryId) {
    	// Try curl http://localhost:8080/factory/factoryname.extensionfactory/start
    	LOGGER.info("Starting a factory simulation.");
    	Factory factory = factoriesSimulated.get(factoryId);
    	
    	if (factory == null) {
    		try {
				factory = (Factory) remoteFactoryPersistenceManager.read(factoryId);
			} catch (IOException ex) {
	        	LOGGER.severe("Failed to retrieve a factory with read(factoryId)");
	            LOGGER.severe(ex.toString());
	            return false;
			}
    		factory.setId(factoryId);
            factoriesSimulated.put(factoryId, factory);
    	}
    	
    	factory.startSimulation();
    	
        return factory.isSimulationStarted();
    }

    @GetMapping("/{factoryId}")
    public ResponseEntity<Factory> getFactory(@PathVariable("factoryId") final String factoryId) {
    	LOGGER.info("Getting a factory.");
        return ResponseEntity.ofNullable(factoriesSimulated.get(factoryId));
    }

    
    @GetMapping("/{factoryId}/stop")
    public boolean stopSimulation(@PathVariable("factoryId") final String factoryId) {
    	LOGGER.info("Stopping a factory simulation.");
    	Factory factory = factoriesSimulated.get(factoryId);
    	
    	if (factory == null) {
    		try {
				factory = (Factory) remoteFactoryPersistenceManager.read(factoryId);
			} catch (IOException ex) {
	        	LOGGER.severe("Failed to retrieve a factory with read(factoryId)");
	            LOGGER.severe(ex.toString());
	            return false;
			}
    		factory.setId(factoryId);
            factoriesSimulated.put(factoryId, factory);
    	}
    	
    	factory.stopSimulation();
    	
        return !factory.isSimulationStarted();
    }
    
}

package com.example.fr.tp.slr201.projects.robotsim.service.simulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@SpringBootApplication
@RestController
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
		// Try curl http://localhost:8080/hello\?name=Saphire
		return String.format("Hello %s!", name);
	}
	
	
	
}

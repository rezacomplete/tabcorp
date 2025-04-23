package com.example.tabcorp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class TabcorpApplication {

	public static void main(String[] args) {
		SpringApplication.run(TabcorpApplication.class, args);
	}

}

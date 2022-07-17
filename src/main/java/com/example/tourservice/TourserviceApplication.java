package com.example.tourservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TourserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TourserviceApplication.class, args);
	}

}

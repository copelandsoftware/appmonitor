package com.emerson.rs.proact.appmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppmonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppmonitorApplication.class, args);
	}

}

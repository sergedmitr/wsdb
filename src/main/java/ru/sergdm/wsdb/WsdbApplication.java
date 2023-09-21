package ru.sergdm.wsdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WsdbApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(WsdbApplication.class, args);
	}

}

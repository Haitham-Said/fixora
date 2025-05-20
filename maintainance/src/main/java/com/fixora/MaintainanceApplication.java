package com.fixora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MaintainanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaintainanceApplication.class, args);
	}

}
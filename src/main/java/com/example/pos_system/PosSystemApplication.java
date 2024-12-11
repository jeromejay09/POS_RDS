package com.example.pos_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.pos")  // Ensure scanning the controller package
public class PosSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(PosSystemApplication.class, args);
	}
}

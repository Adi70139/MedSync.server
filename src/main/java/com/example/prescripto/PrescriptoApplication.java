package com.example.prescripto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PrescriptoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrescriptoApplication.class, args);
	}

}


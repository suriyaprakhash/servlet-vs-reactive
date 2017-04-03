package com.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class RestsampleApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(RestsampleApplication.class, args);
	}

	/*
	 * @Bean CommandLineRunner loadDatabase(PersonRepo hr) { {
	 * 
	 * //hr.findAll().forEach(x -> logger.debug(x.toString())); return args; }
	 * 
	 * }
	 */

	@Autowired
	PersonRepo hr;

	@Override
	public void run(String... arg0) throws Exception {
		hr.save(new Person(1, "Marriott"));
		hr.save(new Person(2, "one"));
		hr.save(new Person(3, "two"));
	}
}

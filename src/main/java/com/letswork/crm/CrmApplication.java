package com.letswork.crm;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CrmApplication {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(CrmApplication.class, args);
	}
	
	@PostConstruct
	public void checkDb() {
	    String db = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
	    System.out.println(">>>> Spring Boot is using database: " + db);
	}

}

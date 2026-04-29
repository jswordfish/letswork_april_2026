package com.letswork.crm.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/util")
public class UtilWebService {
	
	@GetMapping("/get-time-local")
	public ResponseEntity<String> getClientsByCompany(
	        @RequestParam String token
	        ) {
		LocalDateTime current = LocalDateTime.now();
	    return ResponseEntity.ok(current.toString());
	}

}

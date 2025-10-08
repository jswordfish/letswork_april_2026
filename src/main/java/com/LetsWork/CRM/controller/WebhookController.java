package com.LetsWork.CRM.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/webhook")
public class WebhookController {
	
	// Change this to a secure value you choose (or load from application.properties)
	private static final String VERIFY_TOKEN = "my_verify_token_123";


	@GetMapping
	public ResponseEntity<String> verifyWebhook(
	@RequestParam(name = "hub.mode", required = false) String mode,
	@RequestParam(name = "hub.challenge", required = false) String challenge,
	@RequestParam(name = "hub.verify_token", required = false) String token) {


	if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token) && challenge != null) {
	// Return the hub.challenge string (plain text) with HTTP 200
	return ResponseEntity.ok(challenge);
	}
	return ResponseEntity.status(403).body("Invalid verify token");
	}


	@PostMapping
	public ResponseEntity<String> receiveWebhook(@RequestBody String payload) {
	// For now just log the incoming payload so you can see statuses/errors from Meta
	System.out.println("📩 Incoming webhook payload: " + payload);
	return ResponseEntity.ok("EVENT_RECEIVED");
	}

}

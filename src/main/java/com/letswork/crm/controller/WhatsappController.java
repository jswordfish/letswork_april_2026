package com.letswork.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.serviceImpl.WhatsAppServiceImpl;

@RestController
@CrossOrigin
public class WhatsappController {
	
	@Autowired
	WhatsAppServiceImpl service;
	
	@PostMapping("/send-message")
	public String sendMessage(@RequestParam String to, @RequestParam String message, @RequestParam String token) {
//	    service.sendWhatsAppMessage(to, message);
//	    return "Message Sent (Check Logs)";
		return null;
	}

}

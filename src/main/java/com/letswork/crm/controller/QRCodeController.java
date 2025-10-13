package com.letswork.crm.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LetsWork.CRM.service.QRCodeService;
import com.google.zxing.WriterException;

@RestController
@CrossOrigin
public class QRCodeController {
	
	@Autowired
	QRCodeService service;
	
	@PostMapping("/generate QR code")
    public ResponseEntity<String> generateQRCode(
            @RequestParam String text,
            @RequestParam String fileName,
            @RequestParam String token) {
        try {
            String filePath = service.generateQRCode(text, fileName);
            return ResponseEntity.ok("QR Code generated successfully! Saved at: " + filePath);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(500).body("Error generating QR Code: " + e.getMessage());
        }
    }

}

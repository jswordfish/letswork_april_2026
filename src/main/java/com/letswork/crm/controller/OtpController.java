package com.letswork.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.serviceImpl.OtpService;

@RestController
@RequestMapping("/auth")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @RequestParam String email,
            @RequestParam String token) {

        otpService.sendOtp(email);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String token) {

        boolean verified = otpService.verifyOtp(email, otp);

        if (verified) {
            return ResponseEntity.ok("OTP verified successfully");
        }
        return ResponseEntity.badRequest().body("Invalid OTP");
    }
}

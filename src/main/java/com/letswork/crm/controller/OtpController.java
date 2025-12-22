package com.letswork.crm.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.EmailOtp;
import com.letswork.crm.repo.EmailOtpRepository;
import com.letswork.crm.serviceImpl.OtpService;
import com.letswork.crm.util.TokenService2;

@RestController
@RequestMapping("/auth")
public class OtpController {

    @Autowired
    private OtpService otpService;
    
    @Autowired
    EmailOtpRepository emailOtpRepository;
    
    TokenService2 tokenService = new TokenService2();

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @RequestParam String email,
            @RequestParam String companyId) {

        otpService.sendOtp(email, companyId);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {

        boolean verified = otpService.verifyOtp(email, otp);

        if (!verified) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        Optional<EmailOtp> existing =
                emailOtpRepository
                        .findTopByEmailAndVerifiedTrueOrderByExpiresAtDesc(email);

        if (existing.isPresent()
                && Boolean.TRUE.equals(existing.get().getRegistered())) {

            String token = tokenService.generateToken("App User", email);
            return ResponseEntity.ok(token);
        }

        return ResponseEntity.ok("User not registered");
    }
}

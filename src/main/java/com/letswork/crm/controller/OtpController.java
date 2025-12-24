package com.letswork.crm.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.EmailOtp;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.repo.EmailOtpRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.serviceImpl.OtpService;
import com.letswork.crm.util.TokenService2;

@RestController
@RequestMapping("/auth")
public class OtpController {

    @Autowired
    private OtpService otpService;
    
    @Autowired
    EmailOtpRepository emailOtpRepository;
    
    @Autowired
    NewUserRegisterRepository newUserRegisterRepository;
    
    TokenService2 tokenService = new TokenService2();

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @RequestParam String email,
            @RequestParam String companyId) {

        otpService.sendOtp(email, companyId);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String companyId) {

        boolean verified = otpService.verifyOtp(email, otp);

        if (!verified) {
            throw new RuntimeException("Invalid OTP");
        }

        Map<String, Object> response = new HashMap<>();

        Optional<EmailOtp> emailOtp =
                emailOtpRepository
                        .findTopByEmailAndVerifiedTrueOrderByExpiresAtDesc(email);

        if (emailOtp.isPresent()
                && Boolean.TRUE.equals(emailOtp.get().getRegistered())) {

        	NewUserRegister user =
        	        newUserRegisterRepository
        	                .findByEmailAndCompanyId(email, companyId)
        	                .orElseThrow(() -> new RuntimeException("User not found"));

            String token =
                    tokenService.generateToken("App User", email);

            response.put("status", "REGISTERED");
            response.put("token", token);
            response.put("user", user);

            return ResponseEntity.ok(response);
        }

        response.put("status", "OTP_VERIFIED");
        return ResponseEntity.ok(response);
    }
}

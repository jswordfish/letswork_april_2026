package com.letswork.crm.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.User;
import com.letswork.crm.repo.EmailOtpRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.serviceImpl.OtpService;
import com.letswork.crm.util.TokenService2;

@RestController
@RequestMapping("/auth")
public class OtpController {

    @Autowired
    private OtpService otpService;
    
    @Autowired
    UserRepo userRepo;
    
    @Autowired
    EmailOtpRepository emailOtpRepository;
    
    @Autowired
    NewUserRegisterRepository newUserRegisterRepository;
    
    TokenService2 tokenService = new TokenService2();

    @PostMapping("/send-otp-register")
    public ResponseEntity<String> registerSendOtp(
            @RequestParam String email,
            @RequestParam String companyId) {

        String res = otpService.registerSendOtp(email, companyId);
        return ResponseEntity.ok(res);
    }
    
    @PostMapping("/send-otp-login")
    public ResponseEntity<String> loginSendOtp(
            @RequestParam String email,
            @RequestParam String companyId) {

        String res = otpService.loginSendOtp(email, companyId);
        return ResponseEntity.ok(res);
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

        // 1️⃣ Check internal LetsWork user FIRST
        User internalUser = userRepo.findByEmail(email, companyId);

        if (internalUser != null) {

            String token =
                    tokenService.generateToken(
                            internalUser.getRoleOrDesig(),
                            email
                    );

            response.put("status", "INTERNAL_USER");
            response.put("role", internalUser.getRoleOrDesig());
            response.put("token", token);
            response.put("user", internalUser);

            return ResponseEntity.ok(response);
        }

        // 2️⃣ Otherwise App User
        NewUserRegister user =
                newUserRegisterRepository
                        .findByEmailAndCompanyId(email, companyId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        String token =
                tokenService.generateToken(
                        "App User",
                        email
                );

        response.put("status", "REGISTERED");
        response.put("role", "App User");
        response.put("token", token);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }
}

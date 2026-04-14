package com.letswork.crm.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.EmailOtp;
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
    
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @RequestParam String email,
            @RequestParam String companyId) {
    	NewUserRegister newUserRegister = newUserRegisterRepository.findByEmailAndCompanyId(email, companyId).get();
    		if(newUserRegister == null) {
    			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Email Exists!");
    		}
        return ResponseEntity.ok(otpService.sendOtp(email, companyId));
    }
    
    @PostMapping("/reset-credits-mail")
    public ResponseEntity<String> sendResetCreditsMail(@RequestParam String email, @RequestParam String date){
    	
    	return ResponseEntity.ok(otpService.sendResetCreditsEmail(email, date));
    	
    }
    

//    @PostMapping("/verify-otp")
//    public ResponseEntity<Map<String, Object>> verifyOtp(
//            @RequestParam String email,
//            @RequestParam String otp,
//            @RequestParam String companyId) {
//
//        EmailOtp emailOtp = otpService.verifyOtp(email, otp);
//
//        Map<String, Object> response = new HashMap<>();
//
//        // 1️⃣ INTERNAL USER (always login)
//        User internalUser = userRepo.findByEmail(email, companyId);
//        if (internalUser != null) {
//
//            String token =
//                    tokenService.generateToken(
//                            internalUser.getRoleOrDesig(),
//                            email
//                    );
//
//            response.put("status", "INTERNAL_USER");
//            response.put("role", internalUser.getRoleOrDesig());
//            response.put("token", token);
//            response.put("user", internalUser);
//
//            return ResponseEntity.ok(response);
//        }
//
//        // 2️⃣ REGISTRATION OTP → JUST VERIFIED
//        if (Boolean.FALSE.equals(emailOtp.getRegistered())) {
//
//            response.put("status", "OTP_VERIFIED");
//            response.put("message", "OTP verified successfully. Proceed with registration.");
//
//            return ResponseEntity.ok(response);
//        }
//
//        // 3️⃣ LOGIN OTP → USER MUST EXIST
//        NewUserRegister user =
//                newUserRegisterRepository
//                        .findByEmailAndCompanyId(email, companyId)
//                        .orElseThrow(() ->
//                                new RuntimeException("User not registered")
//                        );
//
//        String token =
//                tokenService.generateToken("App User", email);
//
//        response.put("status", "LOGIN_SUCCESS");
//        response.put("role", "App User");
//        response.put("token", token);
//        response.put("user", user);
//
//        return ResponseEntity.ok(response);
//    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String companyId) {

        EmailOtp emailOtp = null;
        Map<String, Object> response = new HashMap<>();

        try {
            emailOtp = otpService.verifyOtp(email, otp);
        } catch (RuntimeException e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }

        // 1️⃣ INTERNAL USER (highest priority)
        User internalUser = userRepo.findByEmail(email, companyId);
        if (internalUser != null) {

            // ✅ NEW CHECK
//            if (Boolean.FALSE.equals(internalUser.getActive())) {
//                response.put("status", "ACCOUNT_DEACTIVATED");
//                response.put("message", "Your account has been deactivated. Please contact support.");
//                return ResponseEntity.ok(response);
//            }

            String token = tokenService.generateToken(
                    internalUser.getRoleOrDesig(),
                    email
            );

            response.put("status", "INTERNAL_USER");
            response.put("role", internalUser.getRoleOrDesig());
            response.put("token", token);
            response.put("user", internalUser);

            return ResponseEntity.ok(response);
        }

        // 2️⃣ REGISTERED USER → LOGIN SUCCESS
        Optional<NewUserRegister> optionalUser =
                newUserRegisterRepository.findByEmailAndCompanyId(email, companyId);

        if (optionalUser.isPresent()) {

            NewUserRegister user = optionalUser.get();

            // ✅ NEW CHECK
            if (Boolean.FALSE.equals(user.getActive())) {
                response.put("status", "ACCOUNT_DEACTIVATED");
                response.put("message", "Your account has been deactivated. Please contact support.");
                return ResponseEntity.ok(response);
            }

            String token = tokenService.generateToken("App User", email);

            response.put("status", "LOGIN_SUCCESS");
            response.put("role", "App User");
            response.put("token", token);
            response.put("user", user);

            return ResponseEntity.ok(response);
        }

        // 3️⃣ NOT REGISTERED
        response.put("status", "NOT_REGISTERED");
        response.put("message", "User not registered. Please proceed with registration.");

        return ResponseEntity.ok(response);
    }
    
}

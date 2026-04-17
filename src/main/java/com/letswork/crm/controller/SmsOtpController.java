package com.letswork.crm.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.serviceImpl.SmsOtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sms-otp")
@RequiredArgsConstructor
public class SmsOtpController {

    private final SmsOtpService smsOtpService;
    
    @Autowired
    NewUserRegisterRepository newUserRegisterRepository;

    @PostMapping("/registerSend")
    public ResponseEntity<String> registerSendOtp(
            @RequestParam String mobile,
            @RequestParam String companyId) {

        String res = smsOtpService.registerSendOtp(mobile, companyId);
        return ResponseEntity.ok(res);
    }
    
    @PostMapping("/loginSend")
    public ResponseEntity<String> loginSendOtp(
            @RequestParam String mobile,
            @RequestParam String companyId) {

        String res = smsOtpService.loginSendOtp(mobile, companyId);
        return ResponseEntity.ok(res);
    }
    
    @PostMapping("/sendSmsOpt")
    public ResponseEntity<String> sendOtp(
            @RequestParam String mobile,
            @RequestParam String companyId) {
    	
    	NewUserRegister newUserRegister = newUserRegisterRepository.findByPhoneNumberAndCompanyId(mobile, companyId).get();
		if(newUserRegister == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No number Exists!");
		}
		if(Boolean.TRUE.equals(newUserRegister.getActive())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account deactivated");
		}

        return ResponseEntity.ok(
                smsOtpService.sendOtp(mobile, companyId)
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(
            @RequestParam String mobile,
            @RequestParam String otp,
            @RequestParam String companyId) {

        Map<String, Object> response =
                smsOtpService.verifyOtp(mobile, otp, companyId);

        return ResponseEntity.ok(response);
    }
}
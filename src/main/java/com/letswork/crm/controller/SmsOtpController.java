package com.letswork.crm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.serviceImpl.SmsOtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sms-otp")
@RequiredArgsConstructor
public class SmsOtpController {

    private final SmsOtpService smsOtpService;

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(
            @RequestParam String mobile) {
        smsOtpService.sendOtp(mobile);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String mobile,
            @RequestParam String otp
    ) {
        smsOtpService.verifyOtp(mobile, otp);
        return ResponseEntity.ok("OTP verified successfully");
    }
}

package com.letswork.crm.controller;

import java.util.Map;

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
            @RequestParam String mobile,
            @RequestParam String companyId) {

        smsOtpService.sendOtp(mobile, companyId);
        return ResponseEntity.ok("OTP sent successfully");
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
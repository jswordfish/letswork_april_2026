package com.letswork.crm.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.SmsOtp;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.SmsOtpRepository;
import com.letswork.crm.util.TokenService2;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsOtpService {

    private final SmsOtpRepository smsOtpRepository;
    private final Msg91SmsService msg91SmsService;
    
    @Autowired
    NewUserRegisterRepository newUserRegisterRepository;
    
    TokenService2 tokenService = new TokenService2();

    private static final int OTP_EXPIRY_MINUTES = 5;

    public void sendOtp(String mobile, String companyId) {

        boolean registered =
                newUserRegisterRepository
                        .findByPhoneNumberAndCompanyId(mobile, companyId)
                        .isPresent();

        String reqId = msg91SmsService.sendOtp(mobile);

        if (reqId == null || reqId.isEmpty()) {
            throw new RuntimeException("Failed to generate OTP");
        }

        SmsOtp smsOtp = SmsOtp.builder()
                .mobile(mobile)
                .reqId(reqId)
                .verified(false)
                .registered(registered)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .createdAt(LocalDateTime.now())
                .build();

        smsOtpRepository.save(smsOtp);
    }

    public Map<String, Object> verifyOtp(
            String mobile,
            String otp,
            String companyId) {

        SmsOtp smsOtp =
                smsOtpRepository
                        .findTopByMobileAndVerifiedFalseOrderByCreatedAtDesc(mobile)
                        .orElseThrow(() ->
                                new RuntimeException("OTP not found"));

        if (smsOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        boolean verified =
                msg91SmsService.verifyOtp(
                        smsOtp.getReqId(),
                        otp
                );

        if (!verified) {
            throw new RuntimeException("Invalid OTP");
        }

        smsOtp.setVerified(true);
        smsOtpRepository.save(smsOtp);

        Map<String, Object> response = new HashMap<>();

        if (Boolean.TRUE.equals(smsOtp.getRegistered())) {

            NewUserRegister user =
                    newUserRegisterRepository
                            .findByPhoneNumberAndCompanyId(mobile, companyId)
                            .orElseThrow(() ->
                                    new RuntimeException("Registered user not found"));

            String token =
                    tokenService.generateToken(
                            "App User",
                            user.getEmail()
                    );

            response.put("status", "REGISTERED");
            response.put("token", token);
            response.put("user", user);

            return response;
        }

        response.put("status", "OTP_VERIFIED");
        return response;
    }
}

package com.letswork.crm.serviceImpl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.letswork.crm.entities.SmsOtp;
import com.letswork.crm.repo.SmsOtpRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsOtpService {

    private final SmsOtpRepository smsOtpRepository;
    private final Msg91SmsService msg91SmsService;

    private static final int OTP_EXPIRY_MINUTES = 5;

    public void sendOtp(String mobile) {

        String reqId = msg91SmsService.sendOtp(mobile);

        SmsOtp smsOtp = SmsOtp.builder()
                .mobile(mobile)
                .reqId(reqId)
                .verified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .createdAt(LocalDateTime.now())
                .build();

        smsOtpRepository.save(smsOtp);
    }

    public boolean verifyOtp(String mobile, String otp) {

        SmsOtp smsOtp = smsOtpRepository
                .findTopByMobileAndVerifiedFalseOrderByCreatedAtDesc(mobile)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (smsOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        boolean verified =
                msg91SmsService.verifyOtp(smsOtp.getReqId(), otp);

        if (!verified) {
            throw new RuntimeException("Invalid OTP");
        }

        smsOtp.setVerified(true);
        smsOtpRepository.save(smsOtp);

        return true;
    }
}

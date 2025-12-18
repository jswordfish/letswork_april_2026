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

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        SmsOtp smsOtp = SmsOtp.builder()
                .mobile(mobile)
                .otp(otp)
                .verified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .createdAt(LocalDateTime.now())
                .build();

        smsOtpRepository.save(smsOtp);

        msg91SmsService.sendOtp(mobile, otp);
    }

    public boolean verifyOtp(String mobile, String otp) {

        SmsOtp smsOtp = smsOtpRepository
                .findTopByMobileAndVerifiedFalseOrderByExpiresAtDesc(mobile)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (smsOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!smsOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        smsOtp.setVerified(true);
        smsOtpRepository.save(smsOtp);

        return true;
    }
}

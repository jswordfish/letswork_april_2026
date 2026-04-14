package com.letswork.crm.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.SmsOtp;
import com.letswork.crm.entities.User;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.SmsOtpRepository;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.util.TokenService2;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsOtpService {

    private final SmsOtpRepository smsOtpRepository;
    private final Msg91SmsService msg91SmsService;
    
    @Autowired
    UserRepo userRepo;
    
    @Autowired
    NewUserRegisterRepository newUserRegisterRepository;
    
    TokenService2 tokenService = new TokenService2();

    private static final int OTP_EXPIRY_MINUTES = 5;
    
    public String sendOtp(String mobile, String companyId) {

        // Optional: delete previous OTPs for same number
        // smsOtpRepository.deleteByMobile(mobile);

        String reqId = msg91SmsService.sendOtp(mobile);

        if (reqId == null || reqId.isEmpty()) {
            throw new RuntimeException("Failed to generate OTP");
        }

        SmsOtp smsOtp = SmsOtp.builder()
                .mobile(mobile)
                .reqId(reqId)
                .verified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .createdAt(LocalDateTime.now())
                .build();

        smsOtpRepository.save(smsOtp);

        return "OTP sent successfully";
    }

    public String registerSendOtp(String mobile, String companyId) {

        boolean registered =
                newUserRegisterRepository
                        .findByPhoneNumberAndCompanyId(mobile, companyId)
                        .isPresent();
        
        if(registered) {
        	return "User is already registered";
        }
        
        else {
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
        return "otp sent successfully";
        }
    }
    
    public String loginSendOtp(String mobile, String companyId) {

        boolean registered =
                newUserRegisterRepository
                        .findByPhoneNumberAndCompanyId(mobile, companyId)
                        .isPresent();
        
        if(!registered) {
        	return "User is not registered";
        }
        
        else {
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
        return "otp sent successfully";
        }
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

        // 1️⃣ INTERNAL USER (if applicable for mobile)
        User internalUser = userRepo.findByPhoneNumber(mobile, companyId);
        if (internalUser != null) {

            String token = tokenService.generateToken(
                    internalUser.getRoleOrDesig(),
                    internalUser.getEmail()
            );

            response.put("status", "INTERNAL_USER");
            response.put("role", internalUser.getRoleOrDesig());
            response.put("token", token);
            response.put("user", internalUser);

            return response;
        }

        // 2️⃣ REGISTERED USER → LOGIN
        Optional<NewUserRegister> optionalUser =
                newUserRegisterRepository
                        .findByPhoneNumberAndCompanyId(mobile, companyId);

        if (optionalUser.isPresent()) {

            NewUserRegister user = optionalUser.get();

            String token = tokenService.generateToken(
                    "App User",
                    user.getEmail()
            );

            response.put("status", "LOGIN_SUCCESS");
            response.put("role", "App User");
            response.put("token", token);
            response.put("user", user);

            return response;
        }

        // 3️⃣ NOT REGISTERED → FRIENDLY MESSAGE
        response.put("status", "NOT_REGISTERED");
        response.put("message", "User not registered. Please proceed with registration.");

        return response;
    }
    
}

package com.letswork.crm.serviceImpl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.EmailOtp;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.repo.EmailOtpRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;

@Service
public class OtpService {

    @Autowired
    private EmailOtpRepository otpRepository;
    
    @Autowired
    private NewUserRegisterRepository newUserRegisterRepository;

    @Autowired
    private MailJetOtpService mailService;

    public void sendOtp(String email, String companyId) {
        String otp = generateOtp();

        EmailOtp emailOtp = new EmailOtp();
        NewUserRegister existing = newUserRegisterRepository.findByEmailAndCompanyId(email, companyId);
        if(existing==null) {
        	emailOtp.setRegistered(false);
        }
        else emailOtp.setRegistered(true);
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        emailOtp.setVerified(false);

        otpRepository.save(emailOtp);

        mailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        EmailOtp emailOtp = otpRepository
                .findTopByEmailAndVerifiedFalseOrderByExpiresAtDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!emailOtp.getOtp().equals(otp)) {
            return false;
        }

        emailOtp.setVerified(true);
        otpRepository.save(emailOtp);
        return true;
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}

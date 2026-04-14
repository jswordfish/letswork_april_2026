package com.letswork.crm.serviceImpl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.EmailOtp;
import com.letswork.crm.entities.User;
import com.letswork.crm.repo.EmailOtpRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.UserRepo;

@Service
public class OtpService {

    @Autowired
    private EmailOtpRepository otpRepository;
    
    @Autowired
    private NewUserRegisterRepository newUserRegisterRepository;
    
    @Autowired
    UserRepo userRepo;

    @Autowired
    private MailJetOtpService mailService;
    
    public String sendOtp(String email, String companyId) {

        String otp = generateOtp();

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        emailOtp.setVerified(false);

        otpRepository.save(emailOtp);

        mailService.sendOtpEmail(email, otp);

        return "OTP sent successfully";
    }

    public String registerSendOtp(String email, String companyId) {

        boolean registered =
                newUserRegisterRepository
                        .findByEmailAndCompanyId(email, companyId)
                        .isPresent();

        User internalUser = userRepo.findByEmail(email, companyId);

        if (registered) {
            return "The user is already registered";
        }

        if (internalUser != null) {
            return "Internal LetsWork staff cannot register";
        }

        String otp = generateOtp();

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        emailOtp.setVerified(false);
        emailOtp.setRegistered(false);

        otpRepository.save(emailOtp);
        mailService.sendOtpEmail(email, otp);

        return "otp sent successfully";
    }
    
    public String loginSendOtp(String email, String companyId) {

        boolean registered =
                newUserRegisterRepository
                        .findByEmailAndCompanyId(email, companyId)
                        .isPresent();

        User internalUser = userRepo.findByEmail(email, companyId);

        if (!registered && internalUser == null) {
            return "User does not exist";
        }

        String otp = generateOtp();

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        emailOtp.setVerified(false);
        emailOtp.setRegistered(registered);

        otpRepository.save(emailOtp);
        mailService.sendOtpEmail(email, otp);

        return "otp sent successfully";
    }

    public EmailOtp verifyOtp(String email, String otp) {

        EmailOtp emailOtp =
            otpRepository.findTopByEmailAndVerifiedFalseOrderByExpiresAtDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!emailOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        emailOtp.setVerified(true);
        otpRepository.save(emailOtp);

        return emailOtp;
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
    
    public String sendResetCreditsEmail(String email, String date) {
    	
    	mailService.sendResetCreditsEmail(email, date);
    	
    	return "Credits Reset email sent";
    	
    }
    
}

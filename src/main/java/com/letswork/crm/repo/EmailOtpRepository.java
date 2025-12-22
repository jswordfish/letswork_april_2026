package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.EmailOtp;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByEmailAndVerifiedFalseOrderByExpiresAtDesc(String email);
    
    Optional<EmailOtp> findTopByEmailAndVerifiedTrueOrderByExpiresAtDesc(String email);
    
}

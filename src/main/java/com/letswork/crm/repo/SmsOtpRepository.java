package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.SmsOtp;

@Repository
public interface SmsOtpRepository extends JpaRepository<SmsOtp, Long> {

    Optional<SmsOtp> findTopByMobileAndVerifiedFalseOrderByExpiresAtDesc(
            String mobile);
}

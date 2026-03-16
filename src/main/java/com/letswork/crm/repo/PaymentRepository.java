package com.letswork.crm.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByPaymentId(String paymentId);
}

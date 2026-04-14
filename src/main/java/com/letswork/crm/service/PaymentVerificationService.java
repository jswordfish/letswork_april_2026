package com.letswork.crm.service;

import com.letswork.crm.dtos.PaymentVerificationResponseDto;

public interface PaymentVerificationService {
	
	public PaymentVerificationResponseDto verifyAndProcessPayment(String paymentId, String referenceId);

}

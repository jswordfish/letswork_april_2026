package com.letswork.crm.service;

public interface PaymentVerificationService {
	
	void verifyAndProcessPayment(String paymentId, String referenceId);

}

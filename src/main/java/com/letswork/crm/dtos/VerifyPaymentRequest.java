package com.letswork.crm.dtos;

import lombok.Data;

@Data
public class VerifyPaymentRequest {
	
	private String paymentId;
	
	private String referenceId;

}

package com.letswork.crm.dtos;

import com.letswork.crm.entities.Booking;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.entities.Payment;

import lombok.Data;

@Data
public class PaymentVerificationResponseDto {
	
	private Invoice invoice;
	
	private Payment payment;
	
	private String razorpayPayment;
	
	private Booking booking;

}

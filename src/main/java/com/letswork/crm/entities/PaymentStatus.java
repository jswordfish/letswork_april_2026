package com.letswork.crm.entities;

public enum PaymentStatus {
	
	CAPTURED("CAPTURED"), AUTHORIZED("AUTHORIZED"), FAILED("FAILED");
	
	String status;
	
	private PaymentStatus(String status) {
		this.status = status;
	}

}

package com.letswork.crm.service;

import org.json.JSONObject;

public interface PaymentService {
	
	JSONObject createPaymentLink(Long invoiceId);

}

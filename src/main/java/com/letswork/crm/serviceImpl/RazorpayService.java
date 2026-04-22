package com.letswork.crm.serviceImpl;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class RazorpayService {

//    private String key = "rzp_live_ulbX2nk9nN1K8x"; //real
//
//    private String secret = "XOyctgFeeDucL8LD3EDL5vIB"; //real
    
	private String key = "rzp_test_SKguaIWs4EkI1g"; //test
    
    private String secret = "o5AgXZLJftz4fmxO9neqOGn7"; //test

    public String createOrder(float amount, String referenceId) {
        try {
            RazorpayClient client = new RazorpayClient(key, secret);

            JSONObject options = new JSONObject();

            // Razorpay expects amount in paise
            options.put("amount", amount*(100));
            options.put("currency", "INR");
            options.put("receipt", referenceId);

            Order order = client.orders.create(options);

            return order.get("id");

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order", e);
        }
    }
}

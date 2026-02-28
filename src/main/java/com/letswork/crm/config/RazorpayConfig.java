package com.letswork.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Configuration
public class RazorpayConfig {

    private String key = "rzp_test_SKguaIWs4EkI1g";

    private String secret = "o5AgXZLJftz4fmxO9neqOGn7";

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(key, secret);
    }
}
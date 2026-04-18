package com.letswork.crm.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class Msg91SmsService {

    
    private String authKey = "483622ARnNMxn2xl69440651P1"; // Dhruv account
	
//	private String authKey = "487978AIop6ncI769e325b9P1"; // letswork account..
    

    private String widgetId = "356c77676263343436333531"; // Dhruv account
    
//    private String widgetId = "366472667641393333383539"; // letswork account

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendOtp(String mobile) {

        String url = "https://api.msg91.com/api/v5/widget/sendOtp";

        HttpHeaders headers = new HttpHeaders();
        headers.set("authkey", authKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("widgetId", widgetId);
        body.put("identifier", "91" + mobile);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> responseBody = response.getBody();

        System.out.println("MSG91 SEND RESPONSE: " + responseBody);

        if (!response.getStatusCode().is2xxSuccessful() || responseBody == null) {
            throw new RuntimeException("Failed to send OTP via MSG91");
        }

        String type = String.valueOf(responseBody.get("type"));

        String reqId = null;

        if (responseBody.get("request_id") != null) {
            reqId = String.valueOf(responseBody.get("request_id"));
        } else if (responseBody.get("reqId") != null) {
            reqId = String.valueOf(responseBody.get("reqId"));
        } else if (responseBody.get("message") != null) {
            reqId = String.valueOf(responseBody.get("message")); // <-- THIS is what you're getting now
        }

        if (!"success".equalsIgnoreCase(type)) {
            throw new RuntimeException("MSG91 Error Response: " + responseBody);
        }

        if (reqId == null || reqId.trim().isEmpty() || "null".equalsIgnoreCase(reqId)) {
            throw new RuntimeException("MSG91 did not return valid request ID: " + responseBody);
        }

        return reqId;
    }

    public boolean verifyOtp(String reqId, String otp) {

        String url = "https://api.msg91.com/api/v5/widget/verifyOtp";

        HttpHeaders headers = new HttpHeaders();
        headers.set("authkey", authKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("widgetId", widgetId);
        body.put("reqId", reqId);
        body.put("otp", otp);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        System.out.println("MSG91 VERIFY RESPONSE: " + response.getBody());

        String type = (String) response.getBody().get("type");
        return "success".equalsIgnoreCase(type);
    }
}
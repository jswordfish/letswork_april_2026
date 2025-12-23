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

    
    private String authKey = "465510ALJwcdtTox068a6e636P1";

    private String widgetId = "356875697332333837383330";

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

        System.out.println("MSG91 SEND RESPONSE: " + response.getBody());

        return (String) response.getBody().get("reqId");
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

        Boolean success = (Boolean) response.getBody().get("success");
        return Boolean.TRUE.equals(success);
    }
}
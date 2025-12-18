package com.letswork.crm.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class Msg91SmsService {

    
    private String authKey = "483622ARnNMxn2xl69440651P1";

    
    private String senderId = "DJ69";

    
    private String templateId = "69440b815185cd2fcf561365";

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtp(String mobile, String otp) {

        String url = "https://api.msg91.com/api/v5/flow/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("authkey", authKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();

        body.put("template_id", templateId);
        body.put("sender", senderId);
        body.put("mobiles", "91" + mobile);

        
        body.put("OTP", otp);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }
}

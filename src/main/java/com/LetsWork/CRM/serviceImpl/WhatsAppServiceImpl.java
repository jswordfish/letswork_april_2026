package com.LetsWork.CRM.serviceImpl;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.LetsWork.CRM.service.WhatsAppService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class WhatsAppServiceImpl implements WhatsAppService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    
    private String baseUrl = "https://graph.facebook.com/v23.0";

    
    private String phoneNumberId = "676000862274443";

    
    private String accessToken = "EAARpd5e53n0BPpYwxH1sKZANDZAiI8C1k6jpPJshB792znxHZCV5ZC6eBRjOIxL2fIaeQDphHxNAUBryjIUu10q27YxGx6UF8i7EyQPxFNEb1pt5d4ouVnNQfz6XFQKcacPWEac89BA6jDc6uMq9H89PrK4ZBrVXI1qSJUatr34zADNbAe5n7GGzBjQ7O8cBi7xmi10A0ztHZCdp2LiE6a1j6cEFrAZCRt7XNHbQmH1DAZDZD";

    @Override
    public String uploadMedia(String filePath) throws Exception {
        String url = baseUrl + "/" + phoneNumberId + "/media";

        File file = new File(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        body.add("type", "image/png"); // adjust if jpg
        body.add("messaging_product", "whatsapp");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to upload media: " + response.getBody());
        }

        JsonNode json = objectMapper.readTree(response.getBody());
        return json.get("id").asText(); // media_id
    }

    @Override
    public void sendImageMessage(String toPhoneNumber, String mediaId, String caption) throws Exception {
        String url = baseUrl + "/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
            {
              "messaging_product": "whatsapp",
              "to": "%s",
              "type": "image",
              "image": {
                "id": "%s",
                "caption": "%s"
              }
            }
        """.formatted(toPhoneNumber, mediaId, caption);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to send image: " + response.getBody());
        }
    }

    @Override
    public void sendBookingQRCode(String toPhoneNumber, String filePath) throws Exception {
        String mediaId = uploadMedia(filePath);
        sendImageMessage(toPhoneNumber, mediaId, "📌 Please scan this QR code to validate your booking");
    }
}
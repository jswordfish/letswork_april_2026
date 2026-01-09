package com.letswork.crm.serviceImpl;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.service.WhatsAppService;

@Service
@Transactional
public class WhatsAppServiceImpl implements WhatsAppService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    
    private String baseUrl = "https://graph.facebook.com/v23.0";

    
    private String phoneNumberId = "676000862274443";

    
    private String accessToken = "EAARpd5e53n0BQfxWCWPEsTf8PWa03VYKoGegbsYFbQK3SFlkf8EHwx5sYR2YPtGcqbf04uap0avYMkh3tFeLMjigrZA4MaARAsOFwnLJGMAZAXMt8936ERbOBuRX7TJiQehGxd1fsPZCftWXZBvjlFSXSNm9lPiAeDfEKAjcp5INjxOi4m1ZB5ZAhpz1SgdASRGW4o756JZA5j7dhfM7eZAd9B5xRbeye0VTMEOqYyllh7WncmkGhhc29hKCUl8ZAXRhIRwZCwUMN0yB7jHtYAqQ4m";

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

        String body = String.format(
                "{\n" +
                        "  \"messaging_product\": \"whatsapp\",\n" +
                        "  \"to\": \"%s\",\n" +
                        "  \"type\": \"image\",\n" +
                        "  \"image\": {\n" +
                        "    \"id\": \"%s\",\n" +
                        "    \"caption\": \"%s\"\n" +
                        "  }\n" +
                        "}", 
                        toPhoneNumber, mediaId, caption
                );

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
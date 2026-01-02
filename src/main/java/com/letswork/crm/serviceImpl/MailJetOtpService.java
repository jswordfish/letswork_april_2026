package com.letswork.crm.serviceImpl;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.Attachment;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TransactionalEmail;

@Service
public class MailJetOtpService {

    private static final String API_KEY = "8259d51f87852f8c7b9f6b08e627f94d";
    private static final String SECRET_KEY = "184fc8e67edae36b2b5ad191e8bd2e53";
    private static final String SENDER_EMAIL = "sales@zimulate.me";

    public void sendOtpEmail(String email, String otp) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);

        Map<String, String> variables = new HashMap<>();
        variables.put("name", "user");
        variables.put("otp", otp);

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Zimulate"))
                .subject("Your OTP Verification Code")
                .templateID(7595302L)   
                .templateLanguage(true)
                .variables(variables)
                .build();

        SendEmailsRequest request = SendEmailsRequest.builder()
                .message(emailMessage)
                .build();

        try {
            request.sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
    
    
    public void sendDayPassEmail(
            String email,
            int numberOfDays,
            String bookingId,
            byte[] qrCodeBytes
    ) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);

        Map<String, Object> variables = new HashMap<>();
        variables.put("bookingId", bookingId);
        variables.put("numberOfDays", numberOfDays);

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Zimulate"))
                .subject("Your Day Pass Booking Confirmation")
                .templateID(7612345L)
                .templateLanguage(true)
                .variables(variables)
                .attachment(
                        Attachment.builder()
                                .contentType("image/png")
                                .filename("day-pass-qr.png")
                                .base64Content(Base64.getEncoder().encodeToString(qrCodeBytes))
                                .build()
                )
                .build();

        SendEmailsRequest request = SendEmailsRequest.builder()
                .message(emailMessage)
                .build();

        try {
            request.sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send Day Pass email", e);
        }
    }

}

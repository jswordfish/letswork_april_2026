package com.letswork.crm.serviceImpl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.repo.BookDayPassRepository;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TransactionalEmail;

@Service
public class MailJetOtpService {
	
	@Autowired
	BookDayPassRepository bookDayPassRepo;

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
            Long bookingId,
            String letsWorkCentre,
            String qrCodePath,
            String name
    ) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);
        
        
        
        String qrImageUrl =
                "http://13.233.33.137:8080/visitor/public/qr?key=" +
                URLEncoder.encode(qrCodePath, StandardCharsets.UTF_8);

        Map<String, Object> variables = new HashMap<>();
        variables.put("bookingId", bookingId);
        variables.put("numberOfDays", numberOfDays);
        variables.put("letsworkCenter", letsWorkCentre);
        variables.put("QR_Code", qrImageUrl);
        variables.put("user", name);

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Zimulate"))
                .subject("Your Day Pass Booking Confirmation")
                .templateID(7636985L)
                .templateLanguage(true)
                .variables(variables)
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
    
    
    public void sendConferenceEmail(
            String email,
            LocalDate dateOfBooking,
            Long bookingId,
            String letsWorkCentre,
            String qrCodePath,
            String name,
            String startTime,
            String endTime,
            String roomName
    ) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);
        
        String qrImageUrl =
                "http://13.233.33.137:8080/visitor/public/qr?key=" +
                URLEncoder.encode(qrCodePath, StandardCharsets.UTF_8);

        Map<String, Object> variables = new HashMap<>();
        variables.put("bookingId", bookingId);
        variables.put("dateOfBooking", dateOfBooking.toString());
        variables.put("letsworkCenter", letsWorkCentre);
        variables.put("QR_Code", qrImageUrl);
        variables.put("conferenceRoomName", roomName);
        variables.put("user", name);
        variables.put("startTime", startTime);
        variables.put("endTime", endTime);

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Zimulate"))
                .subject("Your Conference Room Booking Confirmation")
                .templateID(7637073L)
                .templateLanguage(true)
                .variables(variables)
                .build();

        SendEmailsRequest request = SendEmailsRequest.builder()
                .message(emailMessage)
                .build();

        try {
            request.sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send Conference email", e);
        }
    }
    
    public void sendResetCreditsEmail(String email, String date) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);

        Map<String, String> variables = new HashMap<>();
        variables.put("month_year", date);

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Zimulate"))
                .subject("Credits have been reset for this month")
                .templateID(7657590L)   
                .templateLanguage(true)
                .variables(variables)
                .build();

        SendEmailsRequest request = SendEmailsRequest.builder()
                .message(emailMessage)
                .build();

        try {
            request.sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send credit reset email", e);
        }
    }
    
    public void sendGrevianceEmail(
    		String email,
            String name,
            LocalDateTime dateTime,
            String category,
            String subCategory,
            String letsWorkCentre,
            String issue,
            String keyName
    ) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);
        
        
        
        String qrImageUrl =
                "http://13.233.33.137:8080/visitor/public/qr?key=" +
                URLEncoder.encode(keyName, StandardCharsets.UTF_8);

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("dateandtime", dateTime.toString());
        variables.put("category", category);
        variables.put("subCategory", subCategory);
        variables.put("letswork", letsWorkCentre);
        variables.put("issue", issue);
        variables.put("urlIssue", qrImageUrl);

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Zimulate"))
                .subject("A grievance has been raised")
                .templateID(7700595L)
                .templateLanguage(true)
                .variables(variables)
                .build();

        SendEmailsRequest request = SendEmailsRequest.builder()
                .message(emailMessage)
                .build();

        try {
            request.sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send Grievance email", e);
        }
    }

}

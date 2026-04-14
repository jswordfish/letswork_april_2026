package com.letswork.crm.serviceImpl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
	
//	@Autowired
//	BookDayPassRepository bookDayPassRepo;

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
                "http://letsworkapp.in:8443/visitor/s3/presigned-url/email?key=" +
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
    
    public void sendConferenceBookingThroughBundleEmail(
            String email,
            String name,
            String letsWorkCentre,
            LocalDate dateOfBooking,
            String fromTime,
            String toTime,
            String bookingReference,
            String qrCodePath
    ) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);
        
        String qrImageUrl =
                "https://letsworkapp.in:8443/visitor/s3/presigned-url/email?s3Key=" +
                URLEncoder.encode(qrCodePath, StandardCharsets.UTF_8);

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("letsworkCenter", letsWorkCentre);
        variables.put("dateOfBooking", dateOfBooking.toString());
        variables.put("from", fromTime);
        variables.put("to", toTime);
        variables.put("bookingReference", bookingReference);
        variables.put("QR_Code", qrImageUrl);

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Letswork"))
                .subject("Conference Room Booking (Bundle)")
                .templateID(7861806L)
                .templateLanguage(true)
                .variables(variables)
                .build();

        SendEmailsRequest request = SendEmailsRequest.builder()
                .message(emailMessage)
                .build();

        try {
            request.sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send Conference Bundle email", e);
        }
    }
    
    public void sendDayPassBookingThroughBundleEmail(
            String email,
            String name,
            String letsWorkCentre,
            LocalDate dateOfUse,
            String bookingReference,
            String bundleReference,
            Integer numberOfDays,
            String qrCodePath
    ) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);
        
        String qrImageUrl =
                "https://letsworkapp.in:8443/visitor/s3/presigned-url/email?s3Key=" +
                URLEncoder.encode(qrCodePath, StandardCharsets.UTF_8);

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("letsworkCenter", letsWorkCentre);
        variables.put("dateOfUse", dateOfUse.toString());
        variables.put("bookingReference", bookingReference);
        variables.put("bookingReferenceOfDayPassBundle", bundleReference);
        variables.put("numberOfDays", numberOfDays);
        variables.put("QR_Code", qrImageUrl);

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Letswork"))
                .subject("Day Pass Booking (Bundle)")
                .templateID(7861850L)
                .templateLanguage(true)
                .variables(variables)
                .build();

        SendEmailsRequest request = SendEmailsRequest.builder()
                .message(emailMessage)
                .build();

        try {
            request.sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send Day Pass Bundle email", e);
        }
    }
    
    public void sendConferenceBundlePurchaseEmail(
            String email,
            String name,
            Float numOfHours,
            BigDecimal price,
            LocalDate expiryDate,
            String bookingReference,
            byte[] invoicePdf
    ) {

        MailjetClient client = new MailjetClient(
        		ClientOptions.builder()  
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build()
        );

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("numOfHours", numOfHours);
        variables.put("price", price);
        variables.put("expiryDate", expiryDate.toString());
        variables.put("bookingReference", bookingReference);

        Base64.Encoder encoder = Base64.getEncoder();

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Letswork"))
                .subject("Conference Room Bundle Booking")
                .templateID(7861768L)
                .templateLanguage(true)
                .variables(variables)
                .attachments(List.of(
                	    Attachment.builder()
                	        .filename("invoice.pdf")
                	        .contentType("application/pdf")
                	        .base64Content(Base64.getEncoder().encodeToString(invoicePdf)) // Changed from .content()
                	        .build()
                	))
                .build();

        try {
            SendEmailsRequest.builder().message(emailMessage).build().sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send bundle booking email", e);
        }
    }
    
    public void sendConferenceDirectBookingEmail(
            String email,
            String name,
            String letsworkCenter,
            LocalDate dateOfBooking,
            String startTime,
            String endTime,
            String bookingReference,
            byte[] invoicePdf,
            String qrCodePath
    ) {

    	MailjetClient client = new MailjetClient(
        		ClientOptions.builder()  
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build()
        );
    	
    	String qrImageUrl =
                "https://letsworkapp.in:8443/visitor/s3/presigned-url/email?s3Key=" +
                URLEncoder.encode(qrCodePath, StandardCharsets.UTF_8);

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("letsworkCenter", letsworkCenter);
        variables.put("dateOfBooking", dateOfBooking.toString());
        variables.put("from", startTime);
        variables.put("to", endTime);
        variables.put("bookingReference", bookingReference);
        variables.put("QR_Code", qrImageUrl);

        Base64.Encoder encoder = Base64.getEncoder();

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Letswork"))
                .subject("Conference Room Booking Confirmation")
                .templateID(7861968L)
                .templateLanguage(true)
                .variables(variables)
                .attachments(List.of(
                	    Attachment.builder()
                	        .filename("invoice.pdf")
                	        .contentType("application/pdf")
                	        .base64Content(Base64.getEncoder().encodeToString(invoicePdf))
                	        .build()
                	))
                .build();

        try {
            SendEmailsRequest.builder().message(emailMessage).build().sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send direct booking email", e);
        }
    }
    
    public void sendDayPassBundlePurchaseEmail(
            String email,
            String name,
            Integer numOfDays,
            BigDecimal price,
            LocalDateTime startDate,
            LocalDate expiryDate,
            String letsworkCenter,
            String bookingReference,
            byte[] invoicePdf
    ) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("numOfDays", numOfDays);
        variables.put("price", price);
        variables.put("startDate", startDate.toString());
        variables.put("expiryDate", expiryDate.toString());
        variables.put("letsworkCenter", letsworkCenter);
        variables.put("bookingReference", bookingReference);

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Letswork"))
                .subject("Day Pass Bundle Booking Confirmation")
                .templateID(7861829L)
                .templateLanguage(true)
                .variables(variables)
                .attachments(List.of(
                	    Attachment.builder()
                	        .filename("invoice.pdf")
                	        .contentType("application/pdf")
                	        .base64Content(Base64.getEncoder().encodeToString(invoicePdf))
                	        .build()
                	))
                .build();

        SendEmailsRequest request = SendEmailsRequest.builder()
                .message(emailMessage)
                .build();

        try {
            request.sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send Day Pass Bundle email", e);
        }
    }
    
    public void sendDayPassDirectBookingEmail(
            String email,
            String name,
            String letsworkCenter,
            LocalDate dateOfUse,
            String bookingReference,
            Integer numberOfDays,
            byte[] invoicePdf,
            String qrCodePath
    ) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);
        
        String qrImageUrl =
                "https://letsworkapp.in:8443/visitor/s3/presigned-url/email?s3Key=" +
                URLEncoder.encode(qrCodePath, StandardCharsets.UTF_8);

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("letsworkCenter", letsworkCenter);
        variables.put("dateOfUse", dateOfUse.toString());
        variables.put("bookingReference", bookingReference);
        variables.put("numberOfDays", numberOfDays);
        variables.put("QR_Code", qrImageUrl);

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(List.of(new SendContact(email)))
                .from(new SendContact(SENDER_EMAIL, "Letswork"))
                .subject("Day Pass Booking Confirmation")
                .templateID(7862004L)
                .templateLanguage(true)
                .variables(variables)
                .attachments(List.of(
                	    Attachment.builder()
                	        .filename("invoice.pdf")
                	        .contentType("application/pdf")
                	        .base64Content(Base64.getEncoder().encodeToString(invoicePdf))
                	        .build()
                	))
                .build();

        SendEmailsRequest request = SendEmailsRequest.builder()
                .message(emailMessage)
                .build();

        try {
            request.sendWith(client);
        } catch (MailjetException e) {
            throw new RuntimeException("Failed to send Day Pass Direct email", e);
        }
    }
    
    public void sendVisitorEmail(
    		String nameOfVisited,
    		String letsworkCenter,
    		LocalDate dateOfUse,
    		String visitorName,
    		LocalTime timeOfVisit,
            Integer numberOfGuests,
            String qrCodePath,
            String... emails

           
    ) {

        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(SECRET_KEY)
                .build();

        MailjetClient client = new MailjetClient(options);
        
        
        
        String qrImageUrl =
        		"https://letsworkapp.in:8443/visitor/s3/presigned-url/email?s3Key=" +
                URLEncoder.encode(qrCodePath, StandardCharsets.UTF_8);

        Map<String, Object> variables = new HashMap<>();
        variables.put("letsworkCenter", letsworkCenter);
        variables.put("dateOfUse", dateOfUse.toString());
        variables.put("visitorName", visitorName);
        variables.put("name", nameOfVisited);
        variables.put("timeOfVisit", timeOfVisit.toString());
        variables.put("numberOfGuests", numberOfGuests);
        variables.put("QR_Code", qrImageUrl);
        List<SendContact> list = new ArrayList<>();
        	for(String email : emails) {
        		list.add(new SendContact(email));
        	}

        TransactionalEmail emailMessage = TransactionalEmail.builder()
                .to(list)
                .from(new SendContact(SENDER_EMAIL, "Zimulate"))
                .subject("Visit confirmed")
                .templateID(7922371L)
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

}

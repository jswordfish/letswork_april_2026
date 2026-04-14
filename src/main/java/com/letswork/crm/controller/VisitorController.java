package com.letswork.crm.controller;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.Visitor;
import com.letswork.crm.repo.VisitorRepository;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.VisitorService;
import com.letswork.crm.serviceImpl.MailJetOtpService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;


@RestController
@CrossOrigin
@RequestMapping("/visitor")
@RequiredArgsConstructor
public class VisitorController {
	
	@Autowired
	VisitorService service;
	
	@Autowired
	VisitorRepository repo;
	
	@Autowired
	NewUserRegisterService userService;
	
	@Autowired
	S3Client s3Client;
	
	@Autowired
	MailJetOtpService mailJetOtpService;
	
	private final S3Presigner s3Presigner;
	
	@PostMapping
	public ResponseEntity<Visitor> saveOrUpdate(
	        @RequestBody Visitor visitor,
	        @RequestParam String token
	) {
		Visitor visitor2 = service.saveOrUpdate(visitor);
		
		NewUserRegister user = userService.getByEmailAndCompanyId(visitor.getEmail(), visitor.getCompanyId());
		
		mailJetOtpService.sendVisitorEmail(user.getName(), visitor.getLetsWorkCentre(), visitor2.getVisitDate(), visitor2.getName(), visitor2.getTimeOfVisit(), visitor2.getNumberOfGuests(), visitor2.getQrS3Path(), visitor2.getEmail(), visitor2.getEmailOfVisitor());
	    return ResponseEntity.ok(
	    		visitor2
	    );
	}
	
	@GetMapping("/s3/presigned-url")
	public ResponseEntity<String> getPresignedUrl(
	        @RequestParam String s3Key,
	        @RequestParam String token
	) {

	    PresignedGetObjectRequest presignedRequest =
	            s3Presigner.presignGetObject(p -> p
	                    .getObjectRequest(GetObjectRequest.builder()
	                            .bucket("letsworkcentres")
	                            .key(s3Key)
	                            .build())
	                    .signatureDuration(Duration.ofDays(7))
	            );

	    return ResponseEntity.ok(presignedRequest.url().toString());
	}
	
	@GetMapping("/s3/presigned-url/email")
	public ResponseEntity<Void> getPresignedUrl(@RequestParam String s3Key) {
	    // 1. Generate the fresh URL (valid for a short time or 7 days)
	    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(p -> p
	            .getObjectRequest(GetObjectRequest.builder()
	                    .bucket("letsworkcentres")
	                    .key(s3Key)
	                    .build())
	            .signatureDuration(Duration.ofMinutes(15)) // Can be short since it's used immediately
	    );

	    String actualS3Url = presignedRequest.url().toString();

	    // 2. Redirect the user's browser to the S3 URL
	    return ResponseEntity.status(HttpStatus.FOUND) // HTTP 302
	            .location(URI.create(actualS3Url))
	            .build();
	}
	
	@GetMapping(value = "/public/qr", produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<byte[]> getQrImage(@RequestParam String key) {

	    try (ResponseInputStream<GetObjectResponse> s3Object =
	             s3Client.getObject(GetObjectRequest.builder()
	                     .bucket("letsworkcentres")
	                     .key(key)
	                     .build())) {

	        byte[] imageBytes = s3Object.readAllBytes();

	        return ResponseEntity.ok()
	                .contentType(MediaType.IMAGE_PNG)
	                .cacheControl(CacheControl.noStore())
	                .body(imageBytes);

	    } catch (Exception e) {
	        return ResponseEntity.notFound().build();
	    }
	}
	
//	@GetMapping("/view visitor by date")
//	public List<Visitor> viewByDate(@RequestParam LocalDate visitDate, @RequestParam String token){
//		
//		return service.viewByDate(visitDate);
//		
//	}
	
	@GetMapping("/scan")
	public ResponseEntity<Visitor> scan(
            @RequestParam String qrData,
            @RequestParam String token
    ) {
       
        String bookingCode = qrData.split("\\|")[1];

        Visitor visitor = repo.findByBookingCode(bookingCode);
        
        if(visitor==null) {
        	throw new RuntimeException("Visitor not found");
        }

        return ResponseEntity.ok(visitor);
        
    }
	
	@PostMapping("/allow")
	public ResponseEntity<Visitor> allow(
	        @RequestBody Visitor request,
	        @RequestParam String token
	) {

	    if (Boolean.TRUE.equals(request.getVisited())) {
	        throw new ResponseStatusException(
	                HttpStatus.CONFLICT,
	                "Visitor entry already used"
	        );
	    }

	    LocalDate today = LocalDate.now();

	    if (!today.equals(request.getVisitDate())) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Visitor can only visit on the booking date"
	        );
	    }

	    request.setVisited(true);
	    repo.save(request);

	    return ResponseEntity.ok(request);
	}
	
	
	@GetMapping
	public ResponseEntity<PaginatedResponseDto> filterVisitors(
	        @RequestParam String companyId,
	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String email,
	        @RequestParam(required = false) String emailOfVisitor,
	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate visitDate,
	        @RequestParam(required = false) String letsWorkCentre,
	        @RequestParam(required = false) String city,
	        @RequestParam(required = false) String state,
	        @RequestParam(required = false) String type,
	        @RequestParam String token,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {

	    return ResponseEntity.ok(
	            service.filterPaginated(
	                    companyId,
	                    name,
	                    email,
	                    emailOfVisitor,
	                    visitDate,
	                    letsWorkCentre,
	                    city,
	                    state,
	                    type,
	                    page,
	                    size
	            )
	    );
	}
	
	@GetMapping("/date")
    public ResponseEntity<PaginatedResponseDto> viewByDate(
            @RequestParam LocalDate visitDate,
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.viewByDate(visitDate, companyId, page));
    }
	
	@DeleteMapping
	public String deleteVisitor(@RequestBody Visitor visitor, @RequestParam String token) {
		
		return service.deleteVisitor(visitor);
		
	}

}

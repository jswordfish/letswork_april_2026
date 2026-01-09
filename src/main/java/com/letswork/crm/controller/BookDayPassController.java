package com.letswork.crm.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.DayPassScanResponse;
import com.letswork.crm.entities.BookDayPass;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.repo.BookDayPassRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.service.BookDayPassService;
import com.letswork.crm.serviceImpl.MailJetOtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/book-day-pass")
@RequiredArgsConstructor
public class BookDayPassController {
	
	@Autowired
	MailJetOtpService mailService;
	
	@Autowired
	NewUserRegisterRepository userRepo;
	
	@Autowired
	BookDayPassRepository bookRepo;

    private final BookDayPassService service;

    @PostMapping
    public ResponseEntity<BookDayPass> book(
            @RequestParam String token,
            @RequestBody BookDayPass request
    ) {
    	
    	BookDayPass dayPass = service.book(request);
    	
    	NewUserRegister user = userRepo.findByEmailAndCompanyId(dayPass.getEmail(), dayPass.getCompanyId()).orElseThrow(() -> new RuntimeException("This user does not exists"));
    	
    	mailService.sendDayPassEmail(dayPass.getEmail(), dayPass.getNumberOfDays(), dayPass.getId(), dayPass.getLetsWorkCentre(), dayPass.getQrS3Path(), user.getName());
    	
        return ResponseEntity.ok(dayPass);
    }
    
    @GetMapping("/scan")
    public ResponseEntity<BookDayPass> scanDayPass(
            @RequestParam String qrData,
            @RequestParam String token
    ) {
        // Example qrData: DAYPASS|abc-123
        String bookingCode = qrData.split("\\|")[1];
        
        BookDayPass booking = bookRepo
                .findByBookingCode(bookingCode)
                .orElseThrow(() ->
                        new RuntimeException("Invalid or expired Day Pass")
                );

//        BookDayPass booking = service.scanAndConsume(
//                bookingCode
//        );
//
//        return ResponseEntity.ok(
//                DayPassScanResponse.from(booking)
//        );
        return ResponseEntity.ok(booking);
    }
    
    @PostMapping("/allow")
    public ResponseEntity<BookDayPass> allow(@RequestBody BookDayPass request, @RequestParam String token){
    	
    	int used = request.getUsed();
    	
    	if(used==request.getNumberOfDays()) {
    		throw new RuntimeException("Limit of day passes reached");
    	}
    	
    	request.setUsed(used+1);
    	bookRepo.save(request);
    	
    	return ResponseEntity.ok(request);
    	
    }

    @GetMapping
    public ResponseEntity<List<BookDayPass>> get(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return ResponseEntity.ok(
                service.get(companyId, email, letsWorkCentre, city, state, date)
        );
    }
}

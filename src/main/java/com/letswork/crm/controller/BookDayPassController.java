package com.letswork.crm.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BookDayPass;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.repo.BookDayPassRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
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
	LetsWorkClientRepository letsWorkClientRepository;
	
	@Autowired
	BookDayPassRepository bookRepo;

    private final BookDayPassService service;

    @PostMapping
    public ResponseEntity<BookDayPass> book(
            @RequestParam String token,
            @RequestBody BookDayPass request
    ) {
    	
    	BookDayPass dayPass = service.book(request);
    	
    	LetsWorkClient client = letsWorkClientRepository.findByEmailAndCompanyId(dayPass.getEmail(), dayPass.getCompanyId()).orElseThrow(() -> new RuntimeException("This company does not exists"));
    	
    	mailService.sendDayPassEmail(dayPass.getEmail(), dayPass.getNumberOfDays(), dayPass.getId(), dayPass.getLetsWorkCentre(), dayPass.getQrS3Path(), client.getClientCompanyName());
    	
        return ResponseEntity.ok(dayPass);
    }
    
    @PutMapping("/cancel/{id}")
    public ResponseEntity<BookDayPass> cancel(
            @PathVariable Long id,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.cancel(id, companyId));
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


        return ResponseEntity.ok(booking);
    }
    
    @PutMapping("/reschedule/{id}")
    public ResponseEntity<BookDayPass> reschedule(
            @PathVariable Long id,
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate
    ) {
        return ResponseEntity.ok(service.reschedule(id, newDate, companyId));
    }
    
    @GetMapping("/remaining-daypass")
    public ResponseEntity<Integer> getRemainingDayPass(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam String letsWorkCentre,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {

        return ResponseEntity.ok(
                service.getRemainingDayPass(
                        companyId,
                        letsWorkCentre,
                        city,
                        state,
                        date
                )
        );
    }
    
    @PostMapping("/allow")
    public ResponseEntity<BookDayPass> allow(
            @RequestBody BookDayPass request,
            @RequestParam String token
    ) {

        LocalDate today = LocalDate.now();

        if (!today.equals(request.getDateOfBooking())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Day pass can only be used on the booking date"
            );
        }

        int used = request.getUsed();

        if (used >= request.getNumberOfDays()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Limit of day passes reached"
            );
        }

        request.setUsed(used + 1);
        bookRepo.save(request);

        return ResponseEntity.ok(request);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> get(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                service.getPaginated(
                        companyId,
                        email,
                        letsWorkCentre,
                        city,
                        state,
                        fromDate,
                        toDate,
                        page,
                        size
                )
        );
    }	
}

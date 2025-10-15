package com.letswork.crm.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.BookingValidationResponse;
import com.letswork.crm.entities.Booking;
import com.letswork.crm.service.BookingService;


@RestController
@CrossOrigin
public class BookingController {
	
	@Autowired
	BookingService service;
	
	
	@PostMapping("/create booking")
    public ResponseEntity<Booking> createBooking(
            @RequestParam String clientName,
            @RequestParam String clientEmail,
            @RequestParam String conferenceRoomName,
            @RequestParam String companyId,
            @RequestParam String letsWorkCentre,
            @RequestParam String clientCompanyName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam String token) throws Exception {

        Booking booking = service.createBooking(clientName, clientEmail, conferenceRoomName, companyId, letsWorkCentre, clientCompanyName, startTime, endTime);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/validate booking")
    public ResponseEntity<BookingValidationResponse> validateBooking(@RequestParam String bookingCode, @RequestParam String token) {
        BookingValidationResponse response = service.validateBooking(bookingCode);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cancel booking")
    public String cancelBooking(@RequestParam String bookingCode, @RequestParam String token) {
    	
    	return service.cancelBooking(bookingCode);
    	
    }

}

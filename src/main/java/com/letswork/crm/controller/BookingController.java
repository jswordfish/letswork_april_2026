package com.letswork.crm.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.BookingValidationResponse;
import com.letswork.crm.entities.Booking;
import com.letswork.crm.service.BookingService;


@RestController
@CrossOrigin
@RequestMapping("/booking")
public class BookingController {
	
	@Autowired
	BookingService service;
	
	
	@PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestParam(required = false) String clientEmail,
            @RequestParam String conferenceRoomName,
            @RequestParam String companyId,
            @RequestParam String letsWorkCentre,
            @RequestParam(required = false) String clientCompanyName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String token) throws Exception {

        Booking booking = service.createBooking(clientEmail, conferenceRoomName, companyId, letsWorkCentre, clientCompanyName, startTime, endTime, city, state);
        return ResponseEntity.ok(booking);
    }
	
	@GetMapping
	public ResponseEntity<List<Booking>> getAllBookings(@RequestParam String token) {
	    return ResponseEntity.ok(service.getAllBookings());
	}

    @GetMapping("/validate")
    public ResponseEntity<BookingValidationResponse> validateBooking(@RequestParam String bookingCode, @RequestParam String token) {
        BookingValidationResponse response = service.validateBooking(bookingCode);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cancel")
    public String cancelBooking(@RequestParam String bookingCode, @RequestParam String token) {
    	
    	return service.cancelBooking(bookingCode);
    	
    }

}

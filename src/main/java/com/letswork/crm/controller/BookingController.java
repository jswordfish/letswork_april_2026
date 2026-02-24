package com.letswork.crm.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.BookingValidationResponse;
import com.letswork.crm.dtos.ConferenceRoomBookingDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Booking;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.BookingType;
import com.letswork.crm.service.BookingService;
import com.letswork.crm.serviceImpl.UnifiedBookingsListingService;


@RestController
@CrossOrigin
@RequestMapping("/booking")
public class BookingController {
	
	@Autowired
	BookingService service;
	
	@Autowired
	UnifiedBookingsListingService bookService;
	
	
	@PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestBody ConferenceRoomBookingDto dto,
            @RequestParam String token) throws Exception {

        Booking booking = service.createBooking(dto);
        return ResponseEntity.ok(booking);
    }
	
	@GetMapping
	public ResponseEntity<List<Booking>> getAllBookings(
			@RequestParam String letsWorkCentre,
			@RequestParam String city,
			@RequestParam String state,
			@RequestParam String companyId,
			@RequestParam String token) {
	    return ResponseEntity.ok(service.getBookings(letsWorkCentre, city, state, companyId));
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
    
    @GetMapping("/unified")
    public ResponseEntity<PaginatedResponseDto> getUnifiedBookings(

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

            @RequestParam(required = false) String roomName,
            @RequestParam(required = false) BookingStatus currentStatus,
            @RequestParam(required = false) BookingType bookingType,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                bookService.getUnifiedBookings(
                        companyId,
                        email,
                        letsWorkCentre,
                        city,
                        state,
                        fromDate,
                        toDate,
                        roomName,
                        currentStatus,
                        bookingType,
                        page,
                        size
                )
        );
    }

}

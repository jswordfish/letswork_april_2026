package com.letswork.crm.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.ConferenceBookingDirectRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByConferenceBookingDirect;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.ConferenceRoomTimeSlotRepository;
import com.letswork.crm.service.ConferenceBookingDirectService;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conference-bookings-direct")
@RequiredArgsConstructor
public class ConferenceBookingDirectController {

    private final ConferenceBookingDirectService service;
    private final ConferenceRoomTimeSlotRepository timeSlotRepo;

    @PostMapping
    public ResponseEntity<ConferenceBookingDirect> createDraftBooking(
            @RequestBody ConferenceBookingDirectRequest request,
            @RequestParam String token
    ) {
        return new ResponseEntity<>(service.createDraftBooking(request), HttpStatus.CREATED);
    }
    
    @PostMapping("/cancel")
    public ResponseEntity<ConferenceBookingDirect> cancelBooking(
    		@RequestParam Long bookingId,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        ConferenceBookingDirect booking = service.cancel(bookingId, companyId);
        return ResponseEntity.ok(booking);
    }
    
    @PostMapping("/reschedule")
	public ResponseEntity<ConferenceBookingDirect> rescheduleBooking(@RequestParam Long bookingId,
			@RequestParam String companyId,
			@Parameter(example = "2026-04-09") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
			@RequestBody List<ConferenceRoomSlotRequest> newSlot, @RequestParam String token) {

		ConferenceBookingDirect booking = service.reschedule(bookingId, newDate, newSlot, companyId);

		return ResponseEntity.ok(booking);
	}

    
    @GetMapping("/availability")
    public ResponseEntity<List<ConferenceRoomTimeSlot>> getBookedSlots(
            @RequestParam String companyId,
            @RequestParam Long centreId,
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                timeSlotRepo.findBookedSlots(
                        companyId,
                        centreId,
                        roomId,
                        date
                )
        );
    }
    
    @GetMapping
    public ResponseEntity<PaginatedResponseDto> getDirectBookings(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) String centre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String roomName,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,

            @RequestParam(defaultValue = "DATE_OF_PURCHASE") SortFieldByConferenceBookingDirect bookingDirect,
			@RequestParam(defaultValue = "DESC") SortingOrder order,
			
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                service.getDirectBookings(
                        companyId, clientId, status,
                        centre, city, state, roomName,
                        fromDate, toDate,
                        minPrice, maxPrice,
                        bookingDirect, order,
                        page, size
                )
        );
    }
    
}

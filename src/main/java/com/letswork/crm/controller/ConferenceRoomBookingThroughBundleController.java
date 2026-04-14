package com.letswork.crm.controller;

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

import com.letswork.crm.dtos.ConferenceRoomBundleBookingRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByConferenceThroughBundle;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.service.ConferenceRoomBookingThroughBundleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conference-room-bundle-bookings")
@RequiredArgsConstructor
public class ConferenceRoomBookingThroughBundleController {

    private final ConferenceRoomBookingThroughBundleService service;

    @PostMapping
    public ResponseEntity<List<ConferenceRoomBookingThroughBundle>> bookViaBundle(
            @RequestBody ConferenceRoomBundleBookingRequest request, @RequestParam String token
    ) {
        return new ResponseEntity<>(
                service.bookUsingMultipleBundles(request),
                HttpStatus.CREATED
        );
    }
    
    @PostMapping("/cancel")
    public ResponseEntity<ConferenceRoomBookingThroughBundle> cancelBooking(
    		@RequestParam Long bookingId,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
    	ConferenceRoomBookingThroughBundle booking = service.cancel(bookingId, companyId);
        return ResponseEntity.ok(booking);
    }
    
    @PostMapping("/reschedule")
    public ResponseEntity<ConferenceRoomBookingThroughBundle> rescheduleBooking(
    		@RequestParam Long bookingId,
            @RequestParam String companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
	        @RequestBody List<ConferenceRoomSlotRequest> newSlots,
	        @RequestParam String token
    ) {

    	ConferenceRoomBookingThroughBundle booking = service.reschedule(
                bookingId,
                newDate,
                newSlots,
                companyId
        );

        return ResponseEntity.ok(booking);
    }
    
    @GetMapping
    public ResponseEntity<PaginatedResponseDto> getBookings(
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

            @RequestParam(required = false) Float minHours,
            @RequestParam(required = false) Float maxHours,

            @RequestParam(defaultValue = "DATE_OF_PURCHASE") SortFieldByConferenceThroughBundle sortFieldByThroughBundle,
			@RequestParam(defaultValue = "DESC") SortingOrder order,
			
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                service.getThroughBundleBookings(
                        companyId, clientId, status,
                        centre, city, state, roomName,
                        fromDate, toDate,
                        minHours, maxHours,
                        sortFieldByThroughBundle, order,

                        page, size
                )
        );
    }
    
}
package com.letswork.crm.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.CreateConferenceBundleBookingRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByConferenceBundleBooking;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.service.ConferenceBundleBookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conference-bundle-bookings")
@RequiredArgsConstructor
public class ConferenceBundleBookingController {

    private final ConferenceBundleBookingService conferenceBundleBookingService;

    @PostMapping("createBundleBooking")
    public ResponseEntity<ConferenceBundleBooking> createBundleBooking(
            @RequestBody CreateConferenceBundleBookingRequest request,
            @RequestParam String token
    ) {
        ConferenceBundleBooking booking = conferenceBundleBookingService.createBundlePurchase(
        		request
        );
      // booking.setBookingStatus(BookingStatus.ACTIVE);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }
    
    @PostMapping("deductHoursFromPurchasedConferenceBundle")
    public ResponseEntity<ConferenceBundleBooking> deductHoursFromPurchasedConferenceBundle(
            @RequestParam Long conferenceBundleBookingId, @RequestParam  Float hoursToDeduct,
            @RequestParam String token
    ) {
    	return new ResponseEntity<>(conferenceBundleBookingService.deductBundleWithHours(conferenceBundleBookingId, hoursToDeduct), HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<PaginatedResponseDto> getBundleBookings(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) BookingStatus status,

            @RequestParam(required = false) Float minHours,
            @RequestParam(required = false) Float maxHours,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate expiryFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate expiryTo,

            @RequestParam(defaultValue = "DATE_OF_PURCHASE") SortFieldByConferenceBundleBooking bookingBundleBooking,
			@RequestParam(defaultValue = "DESC") SortingOrder order,
			
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
        		conferenceBundleBookingService.getBundleBookings(
                        companyId, clientId, referenceId, status,
                        fromDate, toDate,
                        minHours, maxHours,
                        expiryFrom, expiryTo,bookingBundleBooking,order,
                        page, size
                )
        );
    }
    
}

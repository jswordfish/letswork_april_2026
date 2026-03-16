package com.letswork.crm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.CreateConferenceBundleBookingRequest;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.enums.BookingStatus;
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
                request.getClientId(),
                request.getBundleId()
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
}

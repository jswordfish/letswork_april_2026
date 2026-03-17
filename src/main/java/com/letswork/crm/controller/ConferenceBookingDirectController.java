package com.letswork.crm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.ConferenceBookingDirectRequest;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.service.ConferenceBookingDirectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conference-bookings-direct")
@RequiredArgsConstructor
public class ConferenceBookingDirectController {

    private final ConferenceBookingDirectService service;

    @PostMapping
    public ResponseEntity<ConferenceBookingDirect> createDraftBooking(
            @RequestBody ConferenceBookingDirectRequest request,
            @RequestParam String token
    ) {
        return new ResponseEntity<>(service.createDraftBooking(request), HttpStatus.CREATED);
    }
}

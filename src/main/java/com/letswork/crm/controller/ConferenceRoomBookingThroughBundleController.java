package com.letswork.crm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.ConferenceRoomBundleBookingRequest;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;
import com.letswork.crm.service.ConferenceRoomBookingThroughBundleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conference-room-bundle-bookings")
@RequiredArgsConstructor
public class ConferenceRoomBookingThroughBundleController {

    private final ConferenceRoomBookingThroughBundleService service;

    @PostMapping
    public ResponseEntity<ConferenceRoomBookingThroughBundle> bookViaBundle(
            @RequestBody ConferenceRoomBundleBookingRequest request, @RequestParam String token
    ) {
        return new ResponseEntity<>(service.bookUsingBundle(request), HttpStatus.CREATED);
    }
}
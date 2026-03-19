package com.letswork.crm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.DayPassBookingThroughBundleRequest;
import com.letswork.crm.entities.DayPassBookingThroughBundle;
import com.letswork.crm.service.DayPassBookingThroughBundleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/daypass-bundle-bookings")
@RequiredArgsConstructor
public class DayPassBookingThroughBundleController {

	private final DayPassBookingThroughBundleService bookingThroughBundleService;

	@PostMapping
	public ResponseEntity<List<DayPassBookingThroughBundle>> dayPassBookViaBundle(
			@RequestBody DayPassBookingThroughBundleRequest request,@RequestParam String token) {
		return new ResponseEntity<>(bookingThroughBundleService.dayPassBookingThroughBundleBooking(request),
				HttpStatus.CREATED);
	}
}

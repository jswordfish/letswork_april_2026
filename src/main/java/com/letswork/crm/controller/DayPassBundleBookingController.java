package com.letswork.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.DayPassBundleBookingRequest;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.service.DayPassBundleBookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/daypass-bundle-bookings")
@RequiredArgsConstructor
public class DayPassBundleBookingController {

	@Autowired
	private DayPassBundleBookingService dayPassBundleBookingService;



	@PostMapping("/createDayPassBundleBooking")
	public ResponseEntity<DayPassBundleBooking> createDayPassBundleBooking(
			@RequestBody DayPassBundleBookingRequest request, @RequestParam String token) {
		DayPassBundleBooking booking = dayPassBundleBookingService.dayPassBundleBooking(request.getClientId(),
				request.getBundleId());

		return new ResponseEntity<>(booking, HttpStatus.CREATED);
	}

	

	
}

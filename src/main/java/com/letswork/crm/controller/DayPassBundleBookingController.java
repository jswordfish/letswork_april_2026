package com.letswork.crm.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.DayPassBundleBookingRequestDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortField;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.service.DayPassBundleBookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/day-pass/bundle")
@RequiredArgsConstructor
public class DayPassBundleBookingController {

	private final DayPassBundleBookingService service;

	// ✅ CREATE BUNDLE BOOKING
	@PostMapping("/create")
	public ResponseEntity<DayPassBundleBooking> createBundleBooking(@RequestBody DayPassBundleBookingRequestDto dto, @RequestParam String token) {

		return ResponseEntity.ok(service.dayPassBundleBooking(dto.getClientId(), dto.getBundleId(), dto.getLetsWorkCentreId(), dto.getBookedFrom()));
	}

	@GetMapping("/all")
	public ResponseEntity<PaginatedResponseDto> getAll(@RequestParam String companyId, @RequestParam String token,

			@RequestParam(required = false) BookingStatus bookingStatus, @RequestParam(required = false) Long clientId,
			@RequestParam(required = false) Long dayPassBundleeId,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

			@RequestParam(required = false) Long centreId,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiryFrom,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiryTo,

			@RequestParam(required = false) Integer remainingDays, @RequestParam(required = false) Boolean paid,
			@RequestParam(defaultValue = "DATE_OF_PURCHASE") SortField sortField,
			@RequestParam(defaultValue = "DESC") SortingOrder sortDir,

			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		return ResponseEntity.ok(service.filterDayPassBundleBooking(companyId, bookingStatus, clientId,
				dayPassBundleeId, date, startDate, endDate, centreId, expiryFrom, expiryTo, remainingDays, paid,
				sortField, sortDir, page, size

		));
	}
}
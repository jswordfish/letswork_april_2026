package com.letswork.crm.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.DayPassBookingThroughBundleRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.DayPassBookingThroughBundle;
import com.letswork.crm.enums.SortFieldByThroughBundle;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.service.DayPassBookingThroughBundleService;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/day-pass/through-bundle")
@RequiredArgsConstructor
public class DayPassBookingThroughBundleController {

	private final DayPassBookingThroughBundleService service;

	@PostMapping("/create")
	public ResponseEntity<List<DayPassBookingThroughBundle>> createBooking(
			@RequestBody DayPassBookingThroughBundleRequest request, @RequestParam String token) {
		return ResponseEntity.ok(service.dayPassBookingThroughBundleBooking(request));
	}
	
	@PostMapping("/cancel")
	public ResponseEntity<DayPassBookingThroughBundle> cancel(@RequestParam String token, @RequestParam Long id,
			@RequestParam String companyId) {

		return ResponseEntity.ok(service.cancelBookingThroughBundle(id, companyId));
	}
	
	@PostMapping("/rescheduleBooking")
	public ResponseEntity<DayPassBookingThroughBundle> rescheduleBooking(@RequestParam String token,
			@RequestParam Long bookingId, @RequestParam String companyId,
			 @Parameter(
				        description = "Date in ISO format",
				        example = "2026-04-09"
				    )
				    @RequestParam
				    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate newDate) {
		return ResponseEntity
				.ok(service.rescheduleBookingThroughBundle(bookingId, newDate, companyId));
	}


	@GetMapping("/all")
	public ResponseEntity<PaginatedResponseDto> getAll(@RequestParam String companyId, @RequestParam String token,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

			@RequestParam(required = false) Long centreId, @RequestParam(required = false) Long bundleId,
			@RequestParam(required = false) Integer days,
			@RequestParam(defaultValue = "DATE_OF_PURCHASE") SortFieldByThroughBundle sortFieldByThroughBundle,
			@RequestParam(defaultValue = "DESC") SortingOrder order, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		return ResponseEntity.ok(service.searchAllDayPassBookingThroughBundle(companyId, date, startDate, endDate,
				centreId, bundleId, days, sortFieldByThroughBundle, order, page, size));
	}
}

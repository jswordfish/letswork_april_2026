package com.letswork.crm.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.DayPassBookingDirectRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.DayPassBookingDirect;
import com.letswork.crm.enums.SortFieldByDirect;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.service.DayPassBookingDirectService;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/day-pass/direct")
@RequiredArgsConstructor
public class DayPassBookingDirectController {

    private final DayPassBookingDirectService service;

    @PostMapping("/create")
    public ResponseEntity<DayPassBookingDirect> createBooking(
            @RequestBody DayPassBookingDirectRequest request,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.createBooking(request));
    }
    
    @PostMapping("/cancel")
	public ResponseEntity<DayPassBookingDirect> cancel(@RequestParam String token, @RequestParam Long id,
			@RequestParam String companyId) {

		return ResponseEntity.ok(service.cancelBookingDirect(id, companyId));
	}
    
    @PostMapping("/rescheduleBooking")
	public ResponseEntity<DayPassBookingDirect> rescheduleBooking(@RequestParam String token,
			@RequestParam Long bookingId,  @Parameter(
			        description = "Date in ISO format",
			        example = "2026-04-09"
			    )
			    @RequestParam
			    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate newDate, @RequestParam String companyId) {

		return ResponseEntity.ok(service.rescheduleBookingDirect(bookingId, newDate, companyId));
	}


    @GetMapping("/all")
    public ResponseEntity<PaginatedResponseDto> getAll(
            @RequestParam String companyId,
            @RequestParam String token,

            @RequestParam(required = false) Long letsWorkCentreId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime date,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) Float minPrice,
            @RequestParam(required = false) Float maxPrice,
            @RequestParam(required = false) Integer passes,
//            @RequestParam(defaultValue = "DATE_OF_PURCHASE") SortFieldByDirect sortFieldByDirect,
//			@RequestParam(defaultValue = "DESC") SortingOrder order, 
            @RequestParam(defaultValue = "DATE_OF_PURCHASE") SortFieldByDirect sortFieldByDirect,
			@RequestParam(defaultValue = "DESC") SortingOrder order, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

    	return ResponseEntity.ok(service.searchAllDayPassBookingDirectService(companyId, letsWorkCentreId, date,
				startDate, endDate, minPrice, maxPrice, passes, sortFieldByDirect, order, page, size));
	}
    
    @GetMapping("/remaining-daypass")
    public ResponseEntity<Integer> getRemainingDayPass(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam String letsWorkCentre,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {

        return ResponseEntity.ok(
                service.getRemainingDayPass(
                        companyId,
                        letsWorkCentre,
                        city,
                        state,
                        date
                )
        );
    }
    
}
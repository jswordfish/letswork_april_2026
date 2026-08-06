package com.letswork.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.DayPassLimit;
import com.letswork.crm.service.DayPassLimitService;

@RestController
@RequestMapping("/day-pass-limit")
public class DayPassLimitController {

    @Autowired
    private DayPassLimitService service;

    @PostMapping("/saveOrUpdate")
    public ResponseEntity<DayPassLimit> saveOrUpdate(
            @RequestBody DayPassLimit dayPassLimit,
            @RequestParam String token
    ) {
    	try {
    	DayPassLimit res = service.saveOrUpdate(dayPassLimit);
        return ResponseEntity.ok(res);
    	}catch (DataIntegrityViolationException ex) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Limit for this centre already exists."
	        );
    	}
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> list(
            @RequestParam String companyId,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String token
    ) {

        return ResponseEntity.ok(
                service.listDayPassLimits(
                        companyId,
                        letsWorkCentre,
                        city,
                        state,
                        page,
                        size
                )
        );
    }
}

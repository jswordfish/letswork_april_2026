package com.letswork.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.BuyDayPassRequestDto;
import com.letswork.crm.dtos.DayPassSummaryResponseDto;
import com.letswork.crm.entities.BuyDayPassBundle;
import com.letswork.crm.service.BuyDayPassBundleService;
import com.letswork.crm.serviceImpl.DayPassSummaryService;

@RestController
@RequestMapping("/buy-day-pass")
public class BuyDayPassBundleController {
	
	@Autowired
	DayPassSummaryService summaryService;

    private final BuyDayPassBundleService service;

    public BuyDayPassBundleController(
            BuyDayPassBundleService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BuyDayPassBundle> purchase(
            @RequestBody BuyDayPassRequestDto dto,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.purchase(dto));
    }

    @GetMapping
    public ResponseEntity<List<BuyDayPassBundle>> get(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long bundleId,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state
    ) {


        return ResponseEntity.ok(
                service.get(
                        companyId,
                        email,
                        bundleId,
                        letsWorkCentre,
                        city,
                        state
                )
        );
    }
    
    
    @GetMapping("/day-pass-summary")
    public ResponseEntity<DayPassSummaryResponseDto> getSummary(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state
    ) {
        return ResponseEntity.ok(
        		summaryService.getSummary(
                        companyId,
                        email,
                        letsWorkCentre,
                        city,
                        state
                )
        );
    }
    
    
}
package com.letswork.crm.controller;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.EnquiryDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Enquiry;
import com.letswork.crm.enums.EnquiryStatus;
import com.letswork.crm.enums.EnquiryType;
import com.letswork.crm.service.EnquiryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enquiry")
@RequiredArgsConstructor
public class EnquiryController {

    private final EnquiryService enquiryService;

    @PostMapping
    public ResponseEntity<Enquiry> createEnquiry(
            @RequestParam String token,
            @RequestBody EnquiryDto dto
    ) {


        return ResponseEntity.ok(
                enquiryService.createEnquiry(dto)
        );
    }
    
    @PutMapping("/status/{id}")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                enquiryService.updateEnquiryStatus(id)
        );
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> getEnquiries(
            @RequestParam String companyId,
            @RequestParam String token,

            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,

            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String search,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            Date fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            Date toDate,

            @RequestParam(required = false) EnquiryType enquiryType,
            
            @RequestParam(required = false) EnquiryStatus enquiryStatus,
            @RequestParam(required = false) String solutionName,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                enquiryService.getEnquiriesPaginated(
                        companyId,
                        name,
                        email,
                        phone,
                        letsWorkCentre,
                        city,
                        state,
                        search,
                        fromDate,
                        toDate,
                        enquiryType,
                        enquiryStatus,
                        solutionName,
                        page,
                        size
                )
        );
    }
}

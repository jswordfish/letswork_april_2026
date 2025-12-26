package com.letswork.crm.controller;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.Enquiry;
import com.letswork.crm.enums.EnquiryType;
import com.letswork.crm.enums.Solution;
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
            @RequestBody Enquiry enquiry
    ) {


        return ResponseEntity.ok(
                enquiryService.createEnquiry(enquiry)
        );
    }

    @GetMapping
    public ResponseEntity<List<Enquiry>> getEnquiries(
            @RequestParam String companyId,
            @RequestParam String token,

            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Solution solution,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            Date fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            Date toDate,
            
            @RequestParam(required = false) EnquiryType enquiryType
    ) {

        return ResponseEntity.ok(
                enquiryService.getEnquiries(
                        companyId,
                        name,
                        email,
                        phone,
                        solution,
                        fromDate,
                        toDate,
                        enquiryType
                )
        );
    }
}

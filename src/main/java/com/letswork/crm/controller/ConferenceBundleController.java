package com.letswork.crm.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBundle;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.service.ConferenceBundleService;

@RestController
@RequestMapping("/conference-bundles")
public class ConferenceBundleController {

    private final ConferenceBundleService service;

    public ConferenceBundleController(
            ConferenceBundleService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConferenceBundle> saveOrUpdate(
            @RequestBody ConferenceBundle bundle,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.saveOrUpdate(bundle));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> getConferenceBundles(
            @RequestParam String companyId,
            @RequestParam String token,

            @RequestParam(required = false) Boolean showInApp,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "DESC") SortingOrder order,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                service.getConferenceBundles(
                        companyId,
                        showInApp,
                        fromDate,
                        toDate,
                        sortBy,
                        order,
                        page,
                        size
                )
        );
    }
}

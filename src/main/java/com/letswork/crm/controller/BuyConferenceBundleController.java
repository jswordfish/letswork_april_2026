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

import com.letswork.crm.dtos.BuyConferenceBundleRequestDto;
import com.letswork.crm.dtos.PaginatedResponseDto;

@RestController
@RequestMapping("/buy-conference-bundle")
public class BuyConferenceBundleController {

//    private final BuyConferenceBundleService service;
//
//    public BuyConferenceBundleController(
//            BuyConferenceBundleService service
//    ) {
//        this.service = service;
//    }
//
//    @PostMapping
//    public ResponseEntity<BuyConferenceBundle> purchase(
//            @RequestBody BuyConferenceBundleRequestDto dto,
//            @RequestParam String token
//    ) {
//        return ResponseEntity.ok(service.purchase(dto));
//    }
//
//    @GetMapping
//    public ResponseEntity<PaginatedResponseDto> get(
//            @RequestParam String companyId,
//            @RequestParam String token,
//            @RequestParam(required = false) String email,
//            @RequestParam(required = false) Long bundleId,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//            LocalDateTime fromDate,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//            LocalDateTime toDate,
//
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return ResponseEntity.ok(
//                service.getPaginated(
//                        companyId,
//                        email,
//                        bundleId,
//                        fromDate,
//                        toDate,
//                        page,
//                        size
//                )
//        );
//    }
}

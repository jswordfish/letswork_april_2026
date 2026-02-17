package com.letswork.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.SeatConfig;
import com.letswork.crm.enums.SeatType;
import com.letswork.crm.service.SeatConfigService;

@RestController
@RequestMapping("/seat-config")
public class SeatConfigController {

    @Autowired
    private SeatConfigService seatConfigService;

    @PostMapping
    public ResponseEntity<SeatConfig> saveOrUpdate(
            @RequestBody SeatConfig seatConfig,
            @RequestParam String token
    ) {
        SeatConfig saved = seatConfigService.saveOrUpdate(seatConfig);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> listSeatConfigs(
            @RequestParam String companyId,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) SeatType seatType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String token
    ) {

        PaginatedResponseDto response =
                seatConfigService.listSeatConfigs(
                        companyId, letsWorkCentre, city, state, seatType, page, size
                );

        return ResponseEntity.ok(response);
    }
}

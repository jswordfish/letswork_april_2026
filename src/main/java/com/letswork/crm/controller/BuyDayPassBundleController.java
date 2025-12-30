package com.letswork.crm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.BuyDayPassRequestDto;
import com.letswork.crm.entities.BuyDayPassBundle;
import com.letswork.crm.service.BuyDayPassBundleService;

@RestController
@RequestMapping("/buy-day-pass")
public class BuyDayPassBundleController {

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
            @RequestParam(required = false) Long bundleId
    ) {
        return ResponseEntity.ok(
                service.get(companyId, email, bundleId)
        );
    }
}
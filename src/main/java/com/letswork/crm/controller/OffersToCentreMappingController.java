package com.letswork.crm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.OfferLetsworkCentreMappingDto;
import com.letswork.crm.entities.OffersToCentreMapping;
import com.letswork.crm.service.OffersToCentreMappingService;

@RestController
@RequestMapping("/offers-centres")
public class OffersToCentreMappingController {

    private final OffersToCentreMappingService service;

    public OffersToCentreMappingController(
            OffersToCentreMappingService service
            
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> saveOrUpdate(
            @RequestBody OfferLetsworkCentreMappingDto dto,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.saveOrUpdate(dto));
    }

    @GetMapping
    public ResponseEntity<List<OffersToCentreMapping>> getByOfferName(
            @RequestParam String offerName,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.getByOfferName(offerName, companyId));
    }
}

package com.letswork.crm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.ConferenceBundle;
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
    public ResponseEntity<List<ConferenceBundle>> getAll(
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.getAllByCompanyId(companyId));
    }
}

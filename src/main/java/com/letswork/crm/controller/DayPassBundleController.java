package com.letswork.crm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.DayPassBundle;
import com.letswork.crm.service.DayPassBundleService;

@RestController
@RequestMapping("/day-pass-bundles")
public class DayPassBundleController {

    private final DayPassBundleService service;

    public DayPassBundleController(
            DayPassBundleService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DayPassBundle> saveOrUpdate(
            @RequestBody DayPassBundle bundle,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.saveOrUpdate(bundle));
    }

    @GetMapping
    public ResponseEntity<List<DayPassBundle>> getAll(
            @RequestParam String companyId,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam String token
    ) {
    	
    	if((letsWorkCentre!=null)&&(city != null)&&(state != null)) {
    		return ResponseEntity.ok(service.getByCentres(letsWorkCentre, companyId, city, state));
    	}
    	
    	else
        return ResponseEntity.ok(service.getAllByCompanyId(companyId));
    }
}

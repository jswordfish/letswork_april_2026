package com.letswork.crm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.DayPassBundleDto;
import com.letswork.crm.entities.DayPassBundle;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.service.DayPassBundleService;
import com.letswork.crm.service.LetsWorkCentreService;

@RestController
@RequestMapping("/day-pass-bundles")
public class DayPassBundleController {

    private final DayPassBundleService service;
    
    private final LetsWorkCentreService letsWorkCentreService;

    public DayPassBundleController(
            DayPassBundleService service, LetsWorkCentreService letsWorkCentreService
    ) {
        this.service = service;
        this.letsWorkCentreService = letsWorkCentreService;
    }

    @PostMapping
    public ResponseEntity<DayPassBundle> saveOrUpdate(
            @RequestBody DayPassBundleDto bundle,
            @RequestParam String token
    ) {
    	LetsWorkCentre letsWorkCentre = letsWorkCentreService.findById(bundle.getLetsWorkCentreId());
    	DayPassBundle dayPassBundle = DayPassBundle.builder()
    			.companyId(letsWorkCentre.getCompanyId())
    			.discountPercentage(bundle.getDiscountPercentage())
    			.letsWorkCentre(letsWorkCentre)
    			.numberOfDays(bundle.getNumberOfDays())
    			.validForDays(bundle.getValidForDays())
    			.price(bundle.getPrice())
    			.build();
    	dayPassBundle = service.saveOrUpdate(dayPassBundle);
    	dayPassBundle.setLetsWorkCentre(letsWorkCentre);
        return ResponseEntity.ok(dayPassBundle);
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

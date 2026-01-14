package com.letswork.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.Offers;
import com.letswork.crm.service.OffersService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@CrossOrigin
public class OffersController {
	
	@Autowired
    private OffersService offersService;

    @PostMapping
    public ResponseEntity<Offers> saveOrUpdateOffer(
            @RequestBody Offers offer,
            @RequestParam String token
    ) {


        Offers saved = offersService.saveOrUpdate(offer);
        return ResponseEntity.ok(saved);
    }
    
    @PostMapping("/disable-offer")
    public ResponseEntity<Offers> disAbleOffer(@RequestBody Offers offer, @RequestParam String token){
    	
    	return ResponseEntity.ok(offersService.disAbleOffer(offer));
    	
    }

    
    @GetMapping
    public ResponseEntity<?> getOffers(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String code
    ) {


        if (code != null && !code.trim().isEmpty()) {

            Offers offer =
                    offersService.getByCodeAndCompanyId(code, companyId);

            return ResponseEntity.ok(offer);
        }

        List<Offers> offers =
                offersService.getAllByCompanyId(companyId);

        return ResponseEntity.ok(offers);
    }
}

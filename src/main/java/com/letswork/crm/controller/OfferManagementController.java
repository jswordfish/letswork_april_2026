package com.letswork.crm.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.OfferCreateRequestDto;
import com.letswork.crm.entities.Offers;
import com.letswork.crm.entities.OffersToCentreMapping;
import com.letswork.crm.service.OfferManagementService;
import com.letswork.crm.service.OffersService;
import com.letswork.crm.service.OffersToCentreMappingService;

@RestController
@RequestMapping("/offers")
public class OfferManagementController {
	
	@Autowired
    OfferManagementService service;
	
	@Autowired
	OffersService offersService;
	
	@Autowired
	OffersToCentreMappingService mappingService;

    

    @PostMapping
    public ResponseEntity<Offers> createOrUpdate(
            @RequestBody OfferCreateRequestDto dto,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                service.createOrUpdateOfferWithCentres(dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<OfferCreateRequestDto>> getOffers(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long letsWorkCentreId   
    ) {

        List<Offers> offers;

        if (code != null && !code.isBlank()) {
            offers = Collections.singletonList(
                    offersService.getByCodeAndCompanyId(code, companyId)
            );
        } else {
            offers = offersService.getAllByCompanyId(companyId);
        }

        List<OfferCreateRequestDto> response = offers.stream()
                .map(offer -> {

                    List<OffersToCentreMapping> mappings =
                            mappingService.getByOfferName(
                                    offer.getName(),
                                    companyId
                            );

                    if (letsWorkCentreId != null) {
                        boolean matches =
                                mappings.stream()
                                        .anyMatch(m ->
                                                m.getLetsWorkCentre().getId()
                                                        .equals(letsWorkCentreId)
                                        );

                        if (!matches) {
                            return null; 
                        }
                    }

                    OfferCreateRequestDto dto = new OfferCreateRequestDto();

                    dto.setName(offer.getName());
                    dto.setCode(offer.getCode());
                    dto.setDiscount(offer.getDiscount());
                    dto.setMinDiscountValue(offer.getMinDiscountValue());
                    dto.setStartDate(offer.getStartDate());
                    dto.setEndDate(offer.getEndDate());
                    dto.setCompanyId(companyId);

                    dto.setCentreIds(
                            mappings.stream()
                                    .map(m -> m.getLetsWorkCentre().getId())
                                    .collect(Collectors.toList())
                    );

                    dto.setLetsWorkCentres(
                            mappings.stream()
                                    .map(OffersToCentreMapping::getLetsWorkCentre)
                                    .collect(Collectors.toList())
                    );

                    return dto;
                })
                .filter(Objects::nonNull) 
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}

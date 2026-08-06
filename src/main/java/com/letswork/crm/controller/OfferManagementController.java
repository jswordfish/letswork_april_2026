package com.letswork.crm.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.OfferCreateRequestDto;
import com.letswork.crm.dtos.OfferCreateResponseDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Offers;
import com.letswork.crm.entities.OffersToCentreMapping;
import com.letswork.crm.enums.OfferType;
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
    	try {
    	Offers res = service.createOrUpdateOfferWithCentres(dto);
        return ResponseEntity.ok(
                res
        );
    	} catch (DataIntegrityViolationException ex) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "This offer code already exists."
	        );
	    }
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> getOffers(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long letsWorkCentreId,
            @RequestParam(required = false) OfferType offerType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<Offers> offerPage = service.getOffers(companyId, code, offerType, search, page, size);

        List<OfferCreateResponseDto> content = offerPage.getContent().stream()
                .map(offer -> {

                    List<OffersToCentreMapping> mappings =
                            mappingService.getByOfferName(offer.getName(), companyId);

                    if (letsWorkCentreId != null) {
                        boolean matches = mappings.stream()
                                .anyMatch(m -> m.getLetsWorkCentre().getId().equals(letsWorkCentreId));
                        if (!matches) {
                            return null;
                        }
                    }

                    OfferCreateResponseDto dto = new OfferCreateResponseDto();
                    dto.setId(offer.getId());
                    dto.setName(offer.getName());
                    dto.setCode(offer.getCode());
                    dto.setDiscount(offer.getDiscount());
                    dto.setMinDiscountValue(offer.getMinDiscountValue());
                    dto.setStartDate(offer.getStartDate());
                    dto.setEndDate(offer.getEndDate());
                    dto.setCompanyId(companyId);
                    dto.setOfferType(offer.getOfferType());
                    dto.setActive(offer.getActive());

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

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom(
                offerPage.getTotalElements() == 0
                        ? 0
                        : page * size + 1
        );

        response.setRecordsTo(
                Math.min(
                        (page + 1) * size,
                        (int) offerPage.getTotalElements()
                )
        );
        response.setTotalNumberOfRecords((int) offerPage.getTotalElements());
        response.setTotalNumberOfPages(offerPage.getTotalPages());
        response.setSelectedPage(page);
        response.setList(content);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping
    public ResponseEntity<Offers> deleteOffer(
            @RequestParam Long offerId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                service.deleteOffer(offerId)
        );
    }
    
}

package com.letswork.crm.serviceImpl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.OfferCreateRequestDto;
import com.letswork.crm.dtos.OfferLetsworkCentreMappingDto;
import com.letswork.crm.entities.Offers;
import com.letswork.crm.service.OfferManagementService;
import com.letswork.crm.service.OffersService;
import com.letswork.crm.service.OffersToCentreMappingService;

@Service
@Transactional
public class OfferManagementServiceImpl implements OfferManagementService {

    private final OffersService offersService;
    private final OffersToCentreMappingService mappingService;

    public OfferManagementServiceImpl(
            OffersService offersService,
            OffersToCentreMappingService mappingService
    ) {
        this.offersService = offersService;
        this.mappingService = mappingService;
    }

    @Override
    public Offers createOrUpdateOfferWithCentres(OfferCreateRequestDto dto) {

        Offers offer = new Offers();
        offer.setName(dto.getName());
        offer.setCode(dto.getCode());
        offer.setDiscount(dto.getDiscount());
        offer.setMinDiscountValue(dto.getMinDiscountValue());
        offer.setStartDate(dto.getStartDate());
        offer.setEndDate(dto.getEndDate());
        offer.setCompanyId(dto.getCompanyId());
        offer.setOfferType(dto.getOfferType());

        Offers savedOffer = offersService.saveOrUpdate(offer);

        OfferLetsworkCentreMappingDto mappingDto =
                new OfferLetsworkCentreMappingDto();

        mappingDto.setOfferName(savedOffer.getName());
        mappingDto.setCompanyId(dto.getCompanyId());
        mappingDto.setCenterIds(dto.getCentreIds());

        mappingService.saveOrUpdate(mappingDto);

        return savedOffer;
    }
}

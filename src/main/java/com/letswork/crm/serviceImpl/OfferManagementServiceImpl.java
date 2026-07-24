package com.letswork.crm.serviceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.OfferCreateRequestDto;
import com.letswork.crm.dtos.OfferLetsworkCentreMappingDto;
import com.letswork.crm.entities.Offers;
import com.letswork.crm.enums.OfferType;
import com.letswork.crm.repo.OffersRepository;
import com.letswork.crm.service.OfferManagementService;
import com.letswork.crm.service.OffersService;
import com.letswork.crm.service.OffersToCentreMappingService;

@Service
@Transactional
public class OfferManagementServiceImpl implements OfferManagementService {
	
	@Autowired
	OffersRepository offersRepository;

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
        offer.setActive(Boolean.TRUE);
        
        

        Offers savedOffer = offersService.saveOrUpdate(offer);

        OfferLetsworkCentreMappingDto mappingDto =
                new OfferLetsworkCentreMappingDto();

        mappingDto.setOfferName(savedOffer.getName());
        mappingDto.setCompanyId(dto.getCompanyId());
        mappingDto.setCenterIds(dto.getCentreIds());

        mappingService.saveOrUpdate(mappingDto);

        return savedOffer;
    }
    
    @Override
    public List<Offers> getOffers(String companyId, String code, OfferType offerType, String search) {

        expireOffers();

        // 🔹 If code is present → return single result
        if (code != null && !code.isBlank()) {

            Offers offer = offersRepository.findByCodeAndCompanyId(code, companyId).orElse(null);

            if (offer == null) {
                return Collections.emptyList();
            }

            return List.of(offer);
        }

        // 🔹 If search is present → search across all fields
        if (search != null && !search.isBlank()) {
            return offersRepository.searchOffers(companyId, search);
        }

        // 🔹 If offerType filter is present
        if (offerType != null) {
            return offersRepository
                    .findAllByCompanyIdAndOfferType(companyId, offerType);
        }

        // 🔹 Default → all offers
        return offersRepository.findAllByCompanyId(companyId);
    }
    
    public void expireOffers() {

        LocalDateTime now = LocalDateTime.now();

        List<Offers> expiredOffers = offersRepository.findExpiredOffers(now);

        for (Offers offer : expiredOffers) {
            offer.setActive(false);
            offer.setUpdateDate(new Date());
        }

        offersRepository.saveAll(expiredOffers);
    }

    // Existing methods (kept for compatibility if used elsewhere)

    @Override
    public Offers getByCodeAndCompanyId(String code, String companyId) {
    	return offersRepository.findByCodeAndCompanyId(code, companyId).orElse(null);
    }

    @Override
    public List<Offers> getAllByCompanyId(String companyId) {
        return offersRepository.findAllByCompanyId(companyId);
    }

	@Override
	public Offers deleteOffer(Long offerId) {
		
		Offers offer = offersRepository.findById(offerId).orElse(null);
		
		if(offer==null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offer not found with id : "+offerId);
		}
		
		offer.setDeleted(Boolean.TRUE);
		
		Offers saved = offersRepository.save(offer);
		
		return saved;
	}

    
}

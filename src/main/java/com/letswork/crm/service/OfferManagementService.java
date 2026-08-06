package com.letswork.crm.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.letswork.crm.dtos.OfferCreateRequestDto;
import com.letswork.crm.entities.Offers;
import com.letswork.crm.enums.OfferType;

public interface OfferManagementService {
	
	Offers createOrUpdateOfferWithCentres(OfferCreateRequestDto dto);
	
	Page<Offers> getOffers(String companyId, String code, OfferType offerType, String search, int page, int size);
	
	public Offers getByCodeAndCompanyId(String code, String companyId);
	
	public List<Offers> getAllByCompanyId(String companyId);
	
	public Offers deleteOffer(Long offerId);

}

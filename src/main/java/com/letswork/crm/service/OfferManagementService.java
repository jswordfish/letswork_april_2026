package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.dtos.OfferCreateRequestDto;
import com.letswork.crm.entities.Offers;
import com.letswork.crm.enums.OfferType;

public interface OfferManagementService {
	
	Offers createOrUpdateOfferWithCentres(OfferCreateRequestDto dto);
	
	List<Offers> getOffers(String companyId, String code, OfferType offerType);
	
	public Offers getByCodeAndCompanyId(String code, String companyId);
	
	public List<Offers> getAllByCompanyId(String companyId);

}

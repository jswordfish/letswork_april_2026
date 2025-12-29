package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.dtos.OfferLetsworkCentreMappingDto;
import com.letswork.crm.entities.OffersToCentreMapping;

public interface OffersToCentreMappingService {
	
	String saveOrUpdate(OfferLetsworkCentreMappingDto dto);

    List<OffersToCentreMapping> getByOfferName(String offerName);

}

package com.letswork.crm.service;

import com.letswork.crm.dtos.OfferCreateRequestDto;
import com.letswork.crm.entities.Offers;

public interface OfferManagementService {
	
	Offers createOrUpdateOfferWithCentres(OfferCreateRequestDto dto);

}

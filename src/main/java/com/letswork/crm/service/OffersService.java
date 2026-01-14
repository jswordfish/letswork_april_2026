package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.Offers;

public interface OffersService {
	
	Offers saveOrUpdate(Offers offer);

    List<Offers> getAllByCompanyId(String companyId);

    Offers getByCodeAndCompanyId(String code, String companyId);
    
    Offers disAbleOffer(Offers offer);

}

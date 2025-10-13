package com.letswork.crm.service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Credit;

public interface CreditService {
	
	Credit saveOrUpdate(Credit credit);
    
    PaginatedResponseDto listAll(String companyId, int page, int size);
    
    void deleteById(Long id);

}

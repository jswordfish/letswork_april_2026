package com.LetsWork.CRM.service;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.Credit;

public interface CreditService {
	
	Credit saveOrUpdate(Credit credit);
    
    PaginatedResponseDto listAll(String companyId, int page, int size);
    
    void deleteById(Long id);

}

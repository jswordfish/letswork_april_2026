package com.letswork.crm.service;

import java.util.Optional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.UserCredit;

public interface UserCreditService {
	
	UserCredit saveOrUpdate(UserCredit userCredit);
    
    UserCredit getByUserEmail(String userEmail, String companyId);
    
    PaginatedResponseDto listAll(String companyId, int page, int size);
    
    void deleteById(Long id);
    
    // Used by the Transaction Service
    UserCredit updateCredits(String userEmail, String companyId, int changeAmount); 
    
    Optional<UserCredit> findById(Long id);

}

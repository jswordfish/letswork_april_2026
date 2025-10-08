package com.LetsWork.CRM.service;

import java.util.Optional;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.UserCredit;

public interface UserCreditService {
	
	UserCredit saveOrUpdate(UserCredit userCredit);
    
    UserCredit getByUserEmail(String userEmail, String companyId);
    
    PaginatedResponseDto listAll(String companyId, int page, int size);
    
    void deleteById(Long id);
    
    // Used by the Transaction Service
    UserCredit updateCredits(String userEmail, String companyId, int changeAmount); 
    
    Optional<UserCredit> findById(Long id);

}

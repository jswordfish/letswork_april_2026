package com.LetsWork.CRM.service;

import java.util.Optional;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.CreditConferenceRoomMapping;

public interface CreditConferenceRoomMappingService {
	
	CreditConferenceRoomMapping saveOrUpdate(CreditConferenceRoomMapping mapping);
	
    PaginatedResponseDto listAll(String companyId, int page, int size);
    
    void deleteById(Long id);
    
    Optional<CreditConferenceRoomMapping> findById(Long id);

}

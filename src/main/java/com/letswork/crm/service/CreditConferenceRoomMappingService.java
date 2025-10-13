package com.letswork.crm.service;

import java.util.Optional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.CreditConferenceRoomMapping;

public interface CreditConferenceRoomMappingService {
	
	CreditConferenceRoomMapping saveOrUpdate(CreditConferenceRoomMapping mapping);
	
    PaginatedResponseDto listAll(String companyId, int page, int size);
    
    void deleteById(Long id);
    
    Optional<CreditConferenceRoomMapping> findById(Long id);

}

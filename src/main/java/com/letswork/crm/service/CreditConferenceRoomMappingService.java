package com.letswork.crm.service;

import java.util.List;
import java.util.Optional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.CreditConferenceRoomMapping;

public interface CreditConferenceRoomMappingService {
	
	CreditConferenceRoomMapping saveOrUpdate(CreditConferenceRoomMapping mapping);
	
    PaginatedResponseDto listAll(String companyId, int page, int size);
    
    List<CreditConferenceRoomMapping> listByCentre(String letsWorkCentre, String room, String companyId, String city, String state);
    
    void deleteById(Long id);
    
    Optional<CreditConferenceRoomMapping> findById(Long id);

}

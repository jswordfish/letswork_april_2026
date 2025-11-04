package com.letswork.crm.service;

import java.util.Optional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.UserSeatMapping;

public interface UserSeatMappingService {
	
	UserSeatMapping saveOrUpdate(UserSeatMapping mapping);

    PaginatedResponseDto listMappings(String companyId, String letsWorkCentre, String city, String state, int pageNo, int pageSize);

    void deleteMapping(Long id);
    
    public PaginatedResponseDto findByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page);
    
    Optional<UserSeatMapping> findByEmail(String email, String companyId, String letsWorkCentre, String city, String state);

}

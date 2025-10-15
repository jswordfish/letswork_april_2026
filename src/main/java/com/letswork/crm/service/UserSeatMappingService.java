package com.letswork.crm.service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.UserSeatMapping;

public interface UserSeatMappingService {
	
	UserSeatMapping saveOrUpdate(UserSeatMapping mapping);

    PaginatedResponseDto listMappings(String companyId, String letsWorkCentre, int pageNo, int pageSize);

    void deleteMapping(Long id);

}

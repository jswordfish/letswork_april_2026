package com.LetsWork.CRM.service;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.UserSeatMapping;

public interface UserSeatMappingService {
	
	UserSeatMapping saveOrUpdate(UserSeatMapping mapping);

    PaginatedResponseDto listMappings(String companyId, String location, int pageNo, int pageSize);

    void deleteMapping(Long id);

}

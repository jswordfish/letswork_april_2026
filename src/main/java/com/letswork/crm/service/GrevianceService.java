package com.letswork.crm.service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Greviance;

public interface GrevianceService {
	
	Greviance saveGreviance(Greviance greviance);

    PaginatedResponseDto getGreviances(
            String companyId,
            String email,
            String centre,
            String city,
            String state,
            int page,
            int size
    );

}

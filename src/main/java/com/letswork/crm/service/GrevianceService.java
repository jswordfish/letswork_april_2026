package com.letswork.crm.service;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Greviance;
import com.letswork.crm.enums.GrevianceStatus;

public interface GrevianceService {
	
	public Greviance saveGreviance(Greviance greviance, MultipartFile image);

	PaginatedResponseDto getGreviances(
	        String companyId,
	        String email,
	        String centre,
	        String city,
	        String state,
	        String category,
	        String subCategory,
	        GrevianceStatus status,
	        int page,
	        int size
	);
    
    Greviance updateGrevianceStatus(
            Long grevianceId,
            GrevianceStatus status,
            String companyId
    );

}

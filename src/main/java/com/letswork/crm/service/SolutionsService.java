package com.letswork.crm.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.SolutionsDto;
import com.letswork.crm.entities.Solutions;

public interface SolutionsService {
	
	public String saveOrUpdate(
			SolutionsDto dto,
            MultipartFile image
    ) throws IOException;
	
//	public List<Solutions> findByCompanyId(String companyId);
	
//	public List<Solutions> findByLetsWorkCentreAndCompanyId(String letsWorkCentre, String companyId);
	
	PaginatedResponseDto getPaginated(
	        String companyId,
	        String letsWorkCentre,
	        String search,
	        int page,
	        int size
	);
	
	public Solutions findByNameAndLetsWorkCentreAndCompanyId(String name, String letsWorkCentre, String companyId);

}

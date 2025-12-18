package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.entities.Solutions;

public interface SolutionsService {
	
	public String saveOrUpdate(
            Solutions solution,
            MultipartFile image
    ) throws IOException;
	
	public List<Solutions> findByCompanyId(String companyId);
	
	public List<Solutions> findByLetsWorkCentreAndCompanyId(String letsWorkCentre, String companyId);
	
	public Solutions findByNameAndLetsWorkCentreAndCompanyId(String name, String letsWorkCentre, String companyId);

}

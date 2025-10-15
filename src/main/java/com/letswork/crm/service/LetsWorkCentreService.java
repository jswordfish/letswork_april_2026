package com.letswork.crm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.LetsWorkCentre;


public interface LetsWorkCentreService {
	
    String saveOrUpdate(LetsWorkCentre letsWorkCentre);
    
    LetsWorkCentre findByName(String name);
    
    List<LetsWorkCentre> findAll();
    
    String deleteLetsWorkCentre(LetsWorkCentre letsWorkCentre);
    
    PaginatedResponseDto getAllLetsWorkCentres(int page);
    
    public String uploadLetsWorkCentresFromExcel(MultipartFile file);
    
}

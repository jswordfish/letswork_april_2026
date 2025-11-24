package com.letswork.crm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.LetsWorkCentre;


public interface LetsWorkCentreService {
	
    String saveOrUpdate(LetsWorkCentre letsWorkCentre);
    
    LetsWorkCentre findByName(String name, String companyId, String city, String state);
    
    List<LetsWorkCentre> findAll(String companyId);
    
    String deleteLetsWorkCentre(LetsWorkCentre letsWorkCentre);
    
    public PaginatedResponseDto getAllLetsWorkCentres(
            int pageNo,
            String companyId,
            String search,
            String sort
    );
    
    List<String> getAllAmenities(String companyId);
    
    public List<String> getAmenitiesForCentre(String name, String companyId, String city, String state);
    
    public String uploadLetsWorkCentresFromExcel(MultipartFile file);
    
}

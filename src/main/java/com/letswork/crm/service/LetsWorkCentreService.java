package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkCentreImage;


public interface LetsWorkCentreService {
	
	public String saveOrUpdate(
	        LetsWorkCentre centre,
	        List<MultipartFile> images,
	        MultipartFile bookTourVideo
	) throws IOException;
    
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
    
    List<LetsWorkCentreImage> getImagesByCentre(
            String centreName,
            String city,
            String state,
            String companyId
    );
    
}

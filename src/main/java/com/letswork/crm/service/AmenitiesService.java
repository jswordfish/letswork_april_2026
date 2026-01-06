package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.entities.Amenities;
import com.letswork.crm.enums.AmenityType;

public interface AmenitiesService {
	
	public Amenities saveOrUpdate(
            Amenities amenities,
            MultipartFile image
    ) throws IOException;

    List<Amenities> listByAmenityType(String companyId, AmenityType type);
    
    List<Amenities> listByCompanyId(String companyId);

    void deleteAmenity(Long id);

}

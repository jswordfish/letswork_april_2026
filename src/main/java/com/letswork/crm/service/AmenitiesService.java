package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.Amenities;
import com.letswork.crm.enums.AmenityType;

public interface AmenitiesService {
	
	Amenities saveOrUpdate(Amenities amenities);

    List<Amenities> listByAmenityType(String companyId, AmenityType type);
    
    List<Amenities> listByCompanyId(String companyId);

    void deleteAmenity(Long id);

}

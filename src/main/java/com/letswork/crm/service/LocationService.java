package com.letswork.crm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Location;


public interface LocationService {
	
    String saveOrUpdate(Location location);
    
    Location findByName(String name);
    
    List<Location> findAll();
    
    String deleteLocation(Location location);
    
    PaginatedResponseDto getAllLocations(int page);
    
    public String uploadLocationsFromExcel(MultipartFile file);
    
}

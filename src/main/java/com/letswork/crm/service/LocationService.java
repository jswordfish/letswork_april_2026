package com.LetsWork.CRM.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.Location;


public interface LocationService {
	
    String saveOrUpdate(Location location);
    
    Location findByName(String name);
    
    List<Location> findAll();
    
    String deleteLocation(Location location);
    
    PaginatedResponseDto getAllLocations(int page);
    
    public String uploadLocationsFromExcel(MultipartFile file);
    
}

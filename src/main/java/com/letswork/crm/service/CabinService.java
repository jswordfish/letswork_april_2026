package com.letswork.crm.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Cabin;
import com.letswork.crm.enums.CabinStatus;

public interface CabinService {
	
	Cabin saveOrUpdate(Cabin cabin);

    PaginatedResponseDto listAll(String companyId, int page, int size);

    void delete(Long id);

    String uploadCabins(MultipartFile file) throws IOException;
    
    PaginatedResponseDto findByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page);
    
    public PaginatedResponseDto listCabins(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            String search,
            String sort,
            int page,
            int size
    );
    
    public Cabin changeCabinStatus(Long cabinId, CabinStatus status);
    
}

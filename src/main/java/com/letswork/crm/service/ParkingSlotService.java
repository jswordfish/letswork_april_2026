package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ParkingSlot;


public interface ParkingSlotService {
	
	String saveOrUpdate(ParkingSlot parkingSlot);

    PaginatedResponseDto listByLetsWorkCentre(String letsWorkCentre, String companyId, int page);

    String deleteParkingSlot(ParkingSlot parkingSlot);
    
    public String uploadParkingSlots(MultipartFile file) throws IOException;

}

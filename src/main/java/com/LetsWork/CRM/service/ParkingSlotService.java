package com.LetsWork.CRM.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.ParkingSlot;


public interface ParkingSlotService {
	
	String saveOrUpdate(ParkingSlot parkingSlot);

    PaginatedResponseDto listByLocation(String location, String companyId, int page);

    String deleteParkingSlot(ParkingSlot parkingSlot);
    
    public List<String> uploadParkingSlots(MultipartFile file, String companyId) throws IOException;

}

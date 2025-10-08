package com.LetsWork.CRM.service;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.ParkingSlot;


public interface ParkingSlotService {
	
	String saveOrUpdate(ParkingSlot parkingSlot);

    PaginatedResponseDto listByLocation(String location, String companyId, int page);

    String deleteParkingSlot(ParkingSlot parkingSlot);

}

package com.letswork.crm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.enums.SeatType;

public interface SeatService {
	
	Seat saveOrUpdate(Seat seat);

    PaginatedResponseDto listSeats(String companyId, String letsWorkCentre, String city, String state, int pageNo, int pageSize);

    void deleteSeat(Long id);
    
    long getTotalSeats(String companyId, String letsWorkCentre, SeatType seatType, String city, String state);
    
    long getAvailableSeats(String companyId, String letsWorkCentre, SeatType seatType, String city, String state);
    
    String uploadSeatExcel(MultipartFile file) throws Exception;

}

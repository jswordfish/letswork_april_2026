package com.LetsWork.CRM.service;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.Seat;
import com.LetsWork.CRM.enums.SeatType;

public interface SeatService {
	
	Seat saveOrUpdate(Seat seat);

    PaginatedResponseDto listSeats(String companyId, String location, int pageNo, int pageSize);

    void deleteSeat(Long id);
    
    long getTotalSeats(String companyId, String location, SeatType seatType);
    
    long getAvailableSeats(String companyId, String location, SeatType seatType);

}

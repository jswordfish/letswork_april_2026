package com.letswork.crm.service;

import java.time.LocalDate;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BookDayPass;

public interface BookDayPassService {
	
	BookDayPass book(BookDayPass request);

	PaginatedResponseDto getPaginated(
	        String companyId,
	        String email,
	        String letsWorkCentre,
	        String city,
	        String state,
	        LocalDate date,
	        int page,
	        int size
	);
    
    public BookDayPass scanAndConsume(String bookingCode);

}

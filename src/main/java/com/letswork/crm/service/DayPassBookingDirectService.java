package com.letswork.crm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.letswork.crm.dtos.DayPassBookingDirectRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.entities.DayPassBookingDirect;
import com.letswork.crm.enums.SortFieldByDirect;
import com.letswork.crm.enums.SortingOrder;

public interface DayPassBookingDirectService {

	DayPassBookingDirect createBooking(DayPassBookingDirectRequest request);

 
//	PaginatedResponseDto searchAllDayPassBookingDirectService(String companyId, Long letsWorkCentreId, LocalDateTime date, LocalDateTime startDate,
//			LocalDateTime endDate, Float minPrice, Float maxPrice,  Integer passes, int page, int size);
	
	PaginatedResponseDto searchAllDayPassBookingDirectService(String companyId, Long letsWorkCentreId, LocalDateTime date, LocalDateTime startDate,
			LocalDateTime endDate, Float minPrice, Float maxPrice,  Integer passes, SortFieldByDirect sortFieldByDirect, SortingOrder order, int page, int size);
	
	public Integer getRemainingDayPass(
	        String companyId,
	        String letsWorkCentre,
	        String city,
	        String state,
	        LocalDate date
	);
	
	//
	DayPassBookingDirect rescheduleBookingDirect(Long bookingId, LocalDate newDate, String companyId) ;
	DayPassBookingDirect cancelBookingDirect(Long id, String companyId) ;
}

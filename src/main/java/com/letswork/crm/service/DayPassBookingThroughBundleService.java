package com.letswork.crm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.letswork.crm.dtos.DayPassBookingThroughBundleRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.DayPassBookingDirect;
import com.letswork.crm.entities.DayPassBookingThroughBundle;
import com.letswork.crm.enums.SortFieldByThroughBundle;
import com.letswork.crm.enums.SortingOrder;

public interface DayPassBookingThroughBundleService {

	public List<DayPassBookingThroughBundle> dayPassBookingThroughBundleBooking(
			DayPassBookingThroughBundleRequest request);

//	public PaginatedResponseDto searchAllDayPassBookingThroughBundle(String companyId, LocalDateTime date, LocalDateTime startDate,
//			LocalDateTime endDate, Long centreId, Long bundleId, Integer days, int page, int size);
	
	public PaginatedResponseDto searchAllDayPassBookingThroughBundle(String companyId, LocalDateTime date,
			LocalDateTime startDate, LocalDateTime endDate, Long centreId, Long bundleId, Integer days,
			SortFieldByThroughBundle sortFieldByThroughBundle, SortingOrder order, int page, int size);
	
	DayPassBookingThroughBundle rescheduleBookingThroughBundle(Long bookingId, LocalDate newDate, String companyId) ;
	public DayPassBookingThroughBundle cancelBookingThroughBundle(Long id, String companyId);
}

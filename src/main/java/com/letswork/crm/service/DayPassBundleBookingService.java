package com.letswork.crm.service;

import java.time.LocalDateTime;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortField;
import com.letswork.crm.enums.SortingOrder;

public interface DayPassBundleBookingService {

	public DayPassBundleBooking dayPassBundleBooking(Long clientId, Long bundleId, Long letsWorkCentreId, BookedFrom bookedFrom);

	public DayPassBundleBooking deductBundleWithDays(Long dayPassBookId, Integer numOfDays);

//	PaginatedResponseDto filterDayPassBundleBooking(String companyId, BookingStatus bookingStatus, Long clientId, Long dayPassBundleeId, LocalDateTime date, LocalDateTime startDate,
//			LocalDateTime endDate, Long centreId,LocalDate expiryFrom,
//			LocalDate expiryTo, Integer remainingDays, Boolean paid, int page, int size);
	
	PaginatedResponseDto filterDayPassBundleBooking(String companyId, BookingStatus bookingStatus, Long clientId,
			Long dayPassBundleeId, LocalDateTime date, LocalDateTime startDate, LocalDateTime endDate, Long centreId,
			LocalDateTime expiryFrom, LocalDateTime expiryTo, Integer remainingDays, Boolean paid, SortField sortField,
			SortingOrder sortDir, int page, int size);

}

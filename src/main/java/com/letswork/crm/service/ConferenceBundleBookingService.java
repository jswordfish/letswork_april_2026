package com.letswork.crm.service;

import java.time.LocalDate;

import com.letswork.crm.dtos.CreateConferenceBundleBookingRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByConferenceBundleBooking;
import com.letswork.crm.enums.SortingOrder;

public interface ConferenceBundleBookingService {
	
	public ConferenceBundleBooking createBundlePurchase(
			CreateConferenceBundleBookingRequest request
    );
	
	public ConferenceBundleBooking deductBundleWithHours(
            Long bundleId, Float hours
    );
	
	PaginatedResponseDto getBundleBookings(
            String companyId,
            Long clientId,
            String referenceId,
            BookingStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            Float minHours,
            Float maxHours,
            LocalDate expiryFrom,
            LocalDate expiryTo,
            SortFieldByConferenceBundleBooking bookingBundleBooking,
	        SortingOrder order,
            int page,
            int size
    );

}

package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.letswork.crm.dtos.ConferenceRoomBundleBookingRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByConferenceThroughBundle;
import com.letswork.crm.enums.SortingOrder;

public interface ConferenceRoomBookingThroughBundleService {
	
	List<ConferenceRoomBookingThroughBundle> bookUsingMultipleBundles(
	        ConferenceRoomBundleBookingRequest request
	);
	
	PaginatedResponseDto getThroughBundleBookings(
            String companyId,
            Long clientId,
            BookingStatus status,
            String centre,
            String city,
            String state,
            String roomName,
            LocalDate fromDate,
            LocalDate toDate,
            Float minHours,
            Float maxHours,
            SortFieldByConferenceThroughBundle throughBundle,
            SortingOrder order,
            int page,
            int size
    );
	
	public ConferenceRoomBookingThroughBundle cancel(Long id, String companyId);
	
	public ConferenceRoomBookingThroughBundle reschedule(Long bookingId, LocalDate newDate,
			List<ConferenceRoomSlotRequest> newSlots, String companyId);

}

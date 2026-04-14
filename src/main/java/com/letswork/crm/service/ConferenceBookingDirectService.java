package com.letswork.crm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.letswork.crm.dtos.ConferenceBookingDirectRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByConferenceBookingDirect;
import com.letswork.crm.enums.SortingOrder;

public interface ConferenceBookingDirectService {
	
	ConferenceBookingDirect createDraftBooking(ConferenceBookingDirectRequest request);
	
	PaginatedResponseDto getDirectBookings(
            String companyId,
            Long clientId,
            BookingStatus status,
            String centre,
            String city,
            String state,
            String roomName,
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            
            SortFieldByConferenceBookingDirect bookingDirect,
            SortingOrder order,
            int page,
            int size
    );
	
	public ConferenceBookingDirect cancel(Long id, String companyId);
	
	public ConferenceBookingDirect reschedule(
            Long bookingId,
            LocalDate newDate,
            List<ConferenceRoomSlotRequest> newSlots,
            String companyId
    );

}

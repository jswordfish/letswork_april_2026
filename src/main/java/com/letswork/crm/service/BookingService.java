package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Booking;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.BookingType;

public interface BookingService {
	
	Booking createDayPassBooking(Booking request);
	
	public Booking createConferenceRoomBooking(
	        Booking request,
	        LocalDate slotDate,
	        List<ConferenceRoomSlotRequest> slotRequests
	);

    PaginatedResponseDto getBookings(
            String companyId,
            String email,
            String centre,
            String city,
            String state,
            BookingType bookingType,
            BookingStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    );


}

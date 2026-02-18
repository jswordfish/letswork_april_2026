package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BookConferenceRoom;
import com.letswork.crm.enums.BookingStatus;


public interface BookConferenceRoomService {
	
	public BookConferenceRoom book(
            BookConferenceRoom request,
            LocalDate slotDate,
            List<ConferenceRoomSlotRequest> slotRequests
    );

    BookConferenceRoom scanAndConsume(String bookingCode);

    public PaginatedResponseDto getPaginated(
            String companyId,
            String email,
            String letsWorkCentre,
            String city,
            String state,
            LocalDate fromDate,
            LocalDate toDate,
            String roomName,
            BookingStatus currentStatus,   
            int page,
            int size
    );

}

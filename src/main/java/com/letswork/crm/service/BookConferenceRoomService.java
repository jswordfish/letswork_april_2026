package com.letswork.crm.service;

import java.time.LocalDate;

import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.entities.BookConferenceRoom;

import java.util.List;


public interface BookConferenceRoomService {
	
	public BookConferenceRoom book(
            BookConferenceRoom request,
            LocalDate slotDate,
            List<ConferenceRoomSlotRequest> slotRequests
    );

    BookConferenceRoom scanAndConsume(String bookingCode);

    List<BookConferenceRoom> get(
            String companyId,
            String email,
            String letsWorkCentre,
            String city,
            String state,
            LocalDate date,
            String roomName
    );

}

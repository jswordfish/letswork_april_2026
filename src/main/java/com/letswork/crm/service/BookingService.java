package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Booking;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByBooking;
import com.letswork.crm.enums.SortingOrder;

public interface BookingService {
	
	Booking save(Booking booking);
	
	PaginatedResponseDto getAllBookings(
	        String companyId,
	        List<String> bookingTypes,
	        Long clientId,
	        String referenceId,
	        List<BookingStatus> status,
	        BookedFrom bookedFrom,
	        String roomName,
	        String search,
	        LocalDate fromDate,
	        LocalDate toDate,
	        LocalDate startDateFromDate,
            LocalDate startDateToDate,
	        SortFieldByBooking sortFieldByBooking,
	        SortingOrder order,
	        int page,
	        int size
	);
	
	void deactivateBooking(Long bookingId);
	
	void deleteDraftBooking(Long bookingId);
	
//	Booking createDayPassBooking(Booking request);
//	
//	public Booking createConferenceRoomBooking(
//	        Booking request,
//	        LocalDate slotDate,
//	        List<ConferenceRoomSlotRequest> slotRequests
//	);
//
//    PaginatedResponseDto getBookings(
//            String companyId,
//            String email,
//            String centre,
//            String city,
//            String state,
//            BookingType bookingType,
//            BookingStatus status,
//            LocalDate fromDate,
//            LocalDate toDate,
//            int page,
//            int size
//    );


}

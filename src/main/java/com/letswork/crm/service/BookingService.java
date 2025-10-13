package com.letswork.crm.service;

import java.time.LocalDateTime;

import com.letswork.crm.dtos.BookingValidationResponse;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Booking;


public interface BookingService {
	
	public Booking createBooking(String clientName, String clientEmail, String conferenceRoomName, String companyId, String location,
            String clientCompanyName, LocalDateTime startTime, LocalDateTime endTime) throws Exception;
	
	public String cancelBooking(String bookingCode);
	
	public BookingValidationResponse validateBooking(String bookingCode);
	
	PaginatedResponseDto listAllBookings(String companyId, int page, int size);

}

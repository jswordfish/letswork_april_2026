package com.LetsWork.CRM.service;

import java.time.LocalDateTime;

import com.LetsWork.CRM.dtos.BookingValidationResponse;
import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.Booking;


public interface BookingService {
	
	public Booking createBooking(String clientName, String clientEmail, String conferenceRoomName, String companyId, String location,
            String clientCompanyName, LocalDateTime startTime, LocalDateTime endTime) throws Exception;
	
	public String cancelBooking(String bookingCode);
	
	public BookingValidationResponse validateBooking(String bookingCode);
	
	PaginatedResponseDto listAllBookings(String companyId, int page, int size);

}

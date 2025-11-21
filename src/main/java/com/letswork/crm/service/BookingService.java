package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.dtos.BookingValidationResponse;
import com.letswork.crm.dtos.ConferenceRoomBookingDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Booking;


public interface BookingService {
	
	public Booking createBooking(ConferenceRoomBookingDto dto) throws Exception;
	
	public String cancelBooking(String bookingCode);
	
	public BookingValidationResponse validateBooking(String bookingCode);
	
	PaginatedResponseDto listAllBookings(String companyId, int page, int size);
	
	public List<Booking> getBookings(String letsWorkCentre, String city, String state, String companyId);

}

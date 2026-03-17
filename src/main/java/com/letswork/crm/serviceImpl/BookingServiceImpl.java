package com.letswork.crm.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Booking;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService{
	
	@Autowired
	BookingRepository bookingRepo;

	@Override
	public Booking save(Booking booking) {
		// TODO Auto-generated method stub
		return bookingRepo.save(booking);
	}

}

package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.ManyToOne;

public class DayPassBookingDirect extends Booking{
	
	private Integer numberOfDays;
	
	private LocalDate dateOfUse;
	
	Float price;
	
	@ManyToOne
	Offers appliedOffer;
	
	Float discountedPrice;

}

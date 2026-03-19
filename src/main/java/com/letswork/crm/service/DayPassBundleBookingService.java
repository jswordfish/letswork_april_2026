package com.letswork.crm.service;

import com.letswork.crm.entities.DayPassBundleBooking;

public interface DayPassBundleBookingService {

	public DayPassBundleBooking dayPassBundleBooking(Long clientId, Long bundleId);
	
	public DayPassBundleBooking deductBundleWithDays(Long dayPassBookId, Integer numOfDays);


//	DayPassBundle
}

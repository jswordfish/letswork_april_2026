package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.dtos.DayPassBookingThroughBundleRequest;
import com.letswork.crm.entities.DayPassBookingThroughBundle;

public interface DayPassBookingThroughBundleService {

	public List<DayPassBookingThroughBundle> dayPassBookingThroughBundleBooking(
			DayPassBookingThroughBundleRequest request);

}

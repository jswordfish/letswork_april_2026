package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.dtos.ConferenceRoomBundleBookingRequest;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;

public interface ConferenceRoomBookingThroughBundleService {
	
	List<ConferenceRoomBookingThroughBundle> bookUsingMultipleBundles(
	        ConferenceRoomBundleBookingRequest request
	);

}

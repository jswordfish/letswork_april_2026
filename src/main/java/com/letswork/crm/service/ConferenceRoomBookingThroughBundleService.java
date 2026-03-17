package com.letswork.crm.service;

import com.letswork.crm.dtos.ConferenceRoomBundleBookingRequest;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;

public interface ConferenceRoomBookingThroughBundleService {
	
	ConferenceRoomBookingThroughBundle bookUsingBundle(ConferenceRoomBundleBookingRequest request);

}

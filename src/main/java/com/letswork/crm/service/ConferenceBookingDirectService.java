package com.letswork.crm.service;

import com.letswork.crm.dtos.ConferenceBookingDirectRequest;
import com.letswork.crm.entities.ConferenceBookingDirect;

public interface ConferenceBookingDirectService {
	
	ConferenceBookingDirect createDraftBooking(ConferenceBookingDirectRequest request);

}

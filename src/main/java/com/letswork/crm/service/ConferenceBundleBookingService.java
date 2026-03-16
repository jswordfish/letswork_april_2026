package com.letswork.crm.service;

import com.letswork.crm.entities.ConferenceBundleBooking;

public interface ConferenceBundleBookingService {
	
	public ConferenceBundleBooking createBundlePurchase(
            Long clientId,
            Long bundleId
    );

}

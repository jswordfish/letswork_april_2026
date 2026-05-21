package com.letswork.crm.dtos;

import lombok.Data;

@Data
public class BundleUsageRequest {
	
	private Long bundleBookingId;
	
    private Float hoursDeducted;

}

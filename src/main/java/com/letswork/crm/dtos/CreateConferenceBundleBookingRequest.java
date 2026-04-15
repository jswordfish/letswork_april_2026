package com.letswork.crm.dtos;

import com.letswork.crm.enums.BookedFrom;

import lombok.Data;

@Data
public class CreateConferenceBundleBookingRequest {
	
    private Long clientId;
    
    private Long bundleId;
    
    private BookedFrom bookedFrom;
    
	private float frontendAmount;
	
	private Integer frontendDiscountPercentage;
	
	private float frontendDiscountedAmount;

    
}

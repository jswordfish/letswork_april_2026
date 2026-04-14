package com.letswork.crm.dtos;

import com.letswork.crm.enums.BookedFrom;

import lombok.Data;

@Data
public class DayPassBundleBookingRequestDto {
	
	Long clientId;
	Long letsWorkCentreId;
	Long bundleId;
	
	BookedFrom bookedFrom = BookedFrom.APP;

}

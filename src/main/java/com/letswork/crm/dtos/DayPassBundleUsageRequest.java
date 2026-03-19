package com.letswork.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayPassBundleUsageRequest {

	private Long dayPassBundleBookingId;

	private Integer daysDeducted;
	
	
	
}

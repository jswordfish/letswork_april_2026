package com.letswork.crm.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayPassBookingThroughBundleRequest {

	private List<DayPassBundleUsageRequest> bundleUsages;

	private Long clientId;

	private Long letsworkCenterId;
	
	private LocalDate dateOfUse;
	
	String companyId;
	
}

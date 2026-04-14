package com.letswork.crm;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.DayPassBookingThroughBundleRequest;
import com.letswork.crm.dtos.DayPassBundleUsageRequest;

public class TestMiscellaneous {
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Test
	public void CreateDayPassBookingThroughBundleRequestJson() throws JsonProcessingException {
		
		List<DayPassBundleUsageRequest> list = Arrays.asList(new DayPassBundleUsageRequest(4146l,1), new DayPassBundleUsageRequest(4146l,1));
		
//		DayPassBookingThroughBundleRequest bookingThroughBundleRequest = DayPassBookingThroughBundleRequest
//				.builder().clientId(4003l)
//				.companyId("LW")
//				.dateOfUse("19/Mar/2026")
//				.letsworkCenterId(3662l)
//				.bundleUsages(list)
//				.build();
//		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookingThroughBundleRequest));
		
	}

}

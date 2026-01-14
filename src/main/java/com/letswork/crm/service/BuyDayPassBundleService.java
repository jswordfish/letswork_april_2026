package com.letswork.crm.service;

import java.time.LocalDateTime;

import com.letswork.crm.dtos.BuyDayPassRequestDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BuyDayPassBundle;

public interface BuyDayPassBundleService {
	
	BuyDayPassBundle purchase(BuyDayPassRequestDto dto);

	public PaginatedResponseDto getPaginated(
	        String companyId,
	        String email,
	        Long bundleId,
	        String letsWorkCentre,
	        String city,
	        String state,
	        LocalDateTime fromDate,
	        LocalDateTime toDate,
	        int page,
	        int size
	);

}

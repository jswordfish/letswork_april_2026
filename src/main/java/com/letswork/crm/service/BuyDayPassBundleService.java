package com.letswork.crm.service;

import com.letswork.crm.dtos.BuyDayPassRequestDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BuyDayPassBundle;

public interface BuyDayPassBundleService {
	
	BuyDayPassBundle purchase(BuyDayPassRequestDto dto);

	PaginatedResponseDto getPaginated(
	        String companyId,
	        String email,
	        Long bundleId,
	        String letsWorkCentre,
	        String city,
	        String state,
	        int page,
	        int size
	);

}

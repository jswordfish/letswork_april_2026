package com.letswork.crm.service;

import java.time.LocalDateTime;

import com.letswork.crm.dtos.BuyConferenceBundleRequestDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BuyConferenceBundle;

public interface BuyConferenceBundleService {
	
	BuyConferenceBundle purchase(BuyConferenceBundleRequestDto dto);

	public PaginatedResponseDto getPaginated(
	        String companyId,
	        String email,
	        Long bundleId,
	        LocalDateTime fromDate,
	        LocalDateTime toDate,
	        int page,
	        int size
	);

}

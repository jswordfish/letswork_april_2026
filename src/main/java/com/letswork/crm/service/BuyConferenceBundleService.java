package com.letswork.crm.service;

import com.letswork.crm.dtos.BuyConferenceBundleRequestDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BuyConferenceBundle;

public interface BuyConferenceBundleService {
	
	BuyConferenceBundle purchase(BuyConferenceBundleRequestDto dto);

	PaginatedResponseDto getPaginated(
	        String companyId,
	        String email,
	        Long bundleId,
	        int page,
	        int size
	);

}

package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.dtos.BuyConferenceBundleRequestDto;
import com.letswork.crm.entities.BuyConferenceBundle;

public interface BuyConferenceBundleService {
	
	BuyConferenceBundle purchase(BuyConferenceBundleRequestDto dto);

    List<BuyConferenceBundle> get(
            String companyId,
            String email,
            Long bundleId
    );

}

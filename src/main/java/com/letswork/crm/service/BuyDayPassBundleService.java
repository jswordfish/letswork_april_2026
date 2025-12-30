package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.dtos.BuyDayPassRequestDto;
import com.letswork.crm.entities.BuyDayPassBundle;

public interface BuyDayPassBundleService {
	
	BuyDayPassBundle purchase(BuyDayPassRequestDto dto);

    List<BuyDayPassBundle> get(
            String companyId,
            String email,
            Long bundleId
    );

}

package com.letswork.crm.service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.DayPassLimit;

public interface DayPassLimitService {
	
	DayPassLimit saveOrUpdate(DayPassLimit dayPassLimit);

    PaginatedResponseDto listDayPassLimits(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            int page,
            int size
    );

}

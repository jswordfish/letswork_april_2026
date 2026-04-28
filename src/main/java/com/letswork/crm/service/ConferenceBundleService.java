package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBundle;
import com.letswork.crm.enums.SortingOrder;

public interface ConferenceBundleService {
	
	ConferenceBundle saveOrUpdate(ConferenceBundle bundle);

	public PaginatedResponseDto getConferenceBundles(
            String companyId,
            Boolean showInApp,
            LocalDate fromDate,
            LocalDate toDate,
            String sortBy,
            SortingOrder order,
            int page,
            int size
    );

}

package com.letswork.crm.service;

import java.time.LocalDate;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Activity;
import com.letswork.crm.enums.ActionType;

public interface ActivityService {
	
	Activity createActivity(Activity activity);

    Activity getById(
            Long id,
            String companyId
    );

    void delete(
            Long id,
            String companyId
    );

    PaginatedResponseDto getPaginated(
            String companyId,
            Long leadId,
            String header,
            ActionType actionType,
            String search,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    );

}

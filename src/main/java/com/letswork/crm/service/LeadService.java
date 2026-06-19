package com.letswork.crm.service;

import java.time.LocalDate;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.enums.LeadQuality;
import com.letswork.crm.enums.LeadStatus;
import com.letswork.crm.enums.Source;

public interface LeadService {
	
	Lead saveOrUpdate(Lead lead);

	PaginatedResponseDto getPaginated(
	        String companyId,
	        String name,
	        String email,
	        String phone,
	        String clientCompanyName,
	        Source source,
	        String location,
	        LeadStatus status,
	        LeadQuality leadQuality,
	        String letsWorkCentre,
	        String city,
	        String state,
	        String solution,
	        String search,
	        LocalDate fromDate,
	        LocalDate toDate,
	        int page,
	        int size
	);
    
    Lead changeStatus(
            Long leadId,
            String companyId,
            LeadStatus status
    );

    void delete(Long id, String companyId);

    Lead getById(Long id, String companyId);

}

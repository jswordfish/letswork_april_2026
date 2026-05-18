package com.letswork.crm.service;

import java.util.Date;

import com.letswork.crm.dtos.EnquiryDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Enquiry;
import com.letswork.crm.enums.EnquiryStatus;
import com.letswork.crm.enums.EnquiryType;

public interface EnquiryService {
	
	Enquiry createEnquiry(EnquiryDto dto);

	PaginatedResponseDto getEnquiriesPaginated(
	        String companyId,
	        String name,
	        String email,
	        String phone,
	        String letsWorkCentre,
	        String city,
	        String state,
	        String search,
	        Date fromDate,
	        Date toDate,
	        EnquiryType enquiryType,
	        EnquiryStatus enquiryStatus,
	        String solutionName,
	        int page,
	        int size
	);
	
	public String updateEnquiryStatus(Long enquiryId);

}

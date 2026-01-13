package com.letswork.crm.service;

import java.util.Date;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Enquiry;
import com.letswork.crm.enums.EnquiryType;
import com.letswork.crm.enums.Solution;

public interface EnquiryService {
	
	Enquiry createEnquiry(Enquiry enquiry);

	PaginatedResponseDto getEnquiriesPaginated(
	        String companyId,
	        String name,
	        String email,
	        String phone,
	        Solution solution,
	        Date fromDate,
	        Date toDate,
	        EnquiryType enquiryType,
	        int page,
	        int size
	);

}

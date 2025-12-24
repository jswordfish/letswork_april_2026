package com.letswork.crm.service;

import java.util.Date;
import java.util.List;

import com.letswork.crm.entities.Enquiry;
import com.letswork.crm.enums.Solution;

public interface EnquiryService {
	
	Enquiry createEnquiry(Enquiry enquiry);

    List<Enquiry> getEnquiries(
            String companyId,
            String name,
            String email,
            String phone,
            Solution solution,
            Date fromDate,
            Date toDate
    );

}

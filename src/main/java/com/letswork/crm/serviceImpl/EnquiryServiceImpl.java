package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Enquiry;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.Solution;
import com.letswork.crm.repo.EnquiryRepository;
import com.letswork.crm.service.EnquiryService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnquiryServiceImpl implements EnquiryService {

    private final EnquiryRepository enquiryRepository;
    
    @Autowired
    TenantService tenantService;

    @Override
    public Enquiry createEnquiry(Enquiry enquiry) {
    	
		Tenant tenant = tenantService.findTenantByCompanyId(enquiry.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+enquiry.getCompanyId());
			
		}

        enquiry.setCreateDate(new Date());
        enquiry.setUpdateDate(new Date());

        return enquiryRepository.save(enquiry);
    }

    @Override
    public List<Enquiry> getEnquiries(
            String companyId,
            String name,
            String email,
            String phone,
            Solution solution,
            Date fromDate,
            Date toDate
    ) {

        return enquiryRepository.findByFilters(
                companyId,
                name,
                email,
                phone,
                solution,
                fromDate,
                toDate
        );
    }
}

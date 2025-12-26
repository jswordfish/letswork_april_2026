package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Enquiry;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.EnquiryType;
import com.letswork.crm.enums.Solution;
import com.letswork.crm.repo.EnquiryRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.EnquiryService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnquiryServiceImpl implements EnquiryService {

    private final EnquiryRepository enquiryRepository;
    
    @Autowired
    TenantService tenantService;
    
    @Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;

    @Override
    public Enquiry createEnquiry(Enquiry enquiry) {
    	
		Tenant tenant = tenantService.findTenantByCompanyId(enquiry.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+enquiry.getCompanyId());
			
		}
		
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(enquiry.getLetsWorkCentre(), enquiry.getCompanyId(), enquiry.getCity(), enquiry.getState());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
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
            Date toDate,
            EnquiryType enquiryType
    ) {

        return enquiryRepository.findByFilters(
                companyId,
                name,
                email,
                phone,
                solution,
                fromDate,
                toDate,
                enquiryType
        );
    }
}

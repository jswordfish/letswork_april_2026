package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.util.Date;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Activity;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.ActionType;
import com.letswork.crm.enums.LeadQuality;
import com.letswork.crm.enums.LeadStatus;
import com.letswork.crm.enums.Source;
import com.letswork.crm.repo.ActivityRepo;
import com.letswork.crm.repo.LeadRepo;
import com.letswork.crm.service.LeadService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class LeadServiceImpl implements LeadService{
	
	@Autowired
	LeadRepo repo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	private ActivityRepo activityRepo;
	
	ModelMapper mapper = new ModelMapper();


	@Override
	public Lead saveOrUpdate(Lead lead) {

	    Tenant tenant = tenantService.findTenantByCompanyId(lead.getCompanyId());

	    if (tenant == null) {
	        throw new RuntimeException(
	                "CompanyId invalid - " + lead.getCompanyId());
	    }

	    if (lead.getId() != null) {

	        Lead existing =
	                repo.findByIdAndCompanyId(
	                        lead.getId(),
	                        lead.getCompanyId());

	        if (existing == null) {
	            throw new RuntimeException("Lead not found");
	        }

	        if (!existing.getEmail().equalsIgnoreCase(lead.getEmail())) {

	            Lead emailLead =
	                    repo.findByEmailAndCompanyId(
	                            lead.getEmail(),
	                            lead.getCompanyId());

	            if (emailLead != null) {
	                throw new RuntimeException(
	                        "Lead already exists with email : "
	                                + lead.getEmail());
	            }
	        }

	        if (!existing.getPhone().equals(lead.getPhone())) {

	            Lead phoneLead =
	                    repo.findByPhoneAndCompanyId(
	                            lead.getPhone(),
	                            lead.getCompanyId());

	            if (phoneLead != null) {
	                throw new RuntimeException(
	                        "Lead already exists with phone : "
	                                + lead.getPhone());
	            }
	        }

	        lead.setCreateDate(existing.getCreateDate());
	        lead.setUpdateDate(new Date());

	        mapper.map(lead, existing);

	        return repo.save(existing);

	    } else {

	        Lead emailLead =
	                repo.findByEmailAndCompanyId(
	                        lead.getEmail(),
	                        lead.getCompanyId());

	        if (emailLead != null) {
	            throw new RuntimeException(
	                    "Lead already exists with email : "
	                            + lead.getEmail());
	        }

	        Lead phoneLead =
	                repo.findByPhoneAndCompanyId(
	                        lead.getPhone(),
	                        lead.getCompanyId());

	        if (phoneLead != null) {
	            throw new RuntimeException(
	                    "Lead already exists with phone : "
	                            + lead.getPhone());
	        }

	        lead.setCreateDate(new Date());

	        Lead savedLead = repo.save(lead);

	        Activity activity = new Activity();
	        activity.setCompanyId(savedLead.getCompanyId());
	        activity.setLeadId(savedLead.getId());
	        activity.setActionType(ActionType.LEAD_CREATED);
	        activity.setHeader(
	                "Lead created at " + new Date()
	        );
	        activity.setCreateDate(new Date());
	        activity.setUpdateDate(new Date());

	        activityRepo.save(activity);

	        return savedLead;
	    }
	}

	@Override
	public PaginatedResponseDto getPaginated(
	        String companyId,
	        String name,
	        String email,
	        String phone,
	        String clientCompanyName,
	        Source source,
	        String location,
	        LeadStatus status,
	        LeadQuality leadQuality,
	        String search,
	        LocalDate fromDate,
	        LocalDate toDate,
	        int page,
	        int size
	) {

	    Pageable pageable =
	            PageRequest.of(page, size, Sort.by("createDate").descending());

	    Page<Lead> resultPage =
	            repo.filter(
	                    companyId,
	                    name,
	                    email,
	                    phone,
	                    clientCompanyName,
	                    source,
	                    location,
	                    status,
	                    leadQuality,
	                    search,
	                    fromDate == null ? null : java.sql.Date.valueOf(fromDate),
	                    toDate == null ? null : java.sql.Date.valueOf(toDate),
	                    pageable
	            );

	    PaginatedResponseDto dto = new PaginatedResponseDto();
	    dto.setSelectedPage(page);
	    dto.setTotalNumberOfRecords((int) resultPage.getTotalElements());
	    dto.setTotalNumberOfPages(resultPage.getTotalPages());

	    dto.setRecordsFrom(
	            resultPage.getTotalElements() == 0
	                    ? 0
	                    : page * size + 1
	    );

	    dto.setRecordsTo(
	            Math.min(
	                    (page + 1) * size,
	                    (int) resultPage.getTotalElements()
	            )
	    );

	    dto.setList(resultPage.getContent());

	    return dto;
	}
	
	@Override
	public Lead changeStatus(
	        Long leadId,
	        String companyId,
	        LeadStatus status
	) {

	    Tenant tenant =
	            tenantService.findTenantByCompanyId(
	                    companyId
	            );

	    if (tenant == null) {

	        throw new RuntimeException(
	                "CompanyId invalid - "
	                        + companyId
	        );
	    }

	    Lead lead =
	            repo.findByIdAndCompanyId(
	                    leadId,
	                    companyId
	            );

	    if (lead == null) {

	        throw new RuntimeException(
	                "Lead not found"
	        );
	    }

	    lead.setStatus(status);
	    lead.setUpdateDate(new Date());

	    return repo.save(lead);
	}

	@Override
	public void delete(Long id, String companyId) {

	    Lead lead =
	            repo.findByIdAndCompanyId(id, companyId);

	    if (lead == null) {
	        throw new RuntimeException("Lead not found");
	    }

	    repo.delete(lead);
	}

	@Override
	public Lead getById(Long id, String companyId) {

	    Lead lead =
	            repo.findByIdAndCompanyId(id, companyId);

	    if (lead == null) {
	        throw new RuntimeException("Lead not found");
	    }

	    return lead;
	}

}

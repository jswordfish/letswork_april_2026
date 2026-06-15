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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Activity;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.ActionType;
import com.letswork.crm.repo.ActivityRepo;
import com.letswork.crm.repo.LeadRepo;
import com.letswork.crm.service.ActivityService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class ActivityServiceImpl implements ActivityService{
	
	@Autowired
	private LeadRepo leadRepo;

	@Autowired
	private TenantService tenantService;
	
	@Autowired
	private ActivityRepo repo;
	
	private ModelMapper mapper = new ModelMapper();

	@Override
	public Activity createActivity(
	        Activity activity
	) {

	    Tenant tenant =
	            tenantService.findTenantByCompanyId(
	                    activity.getCompanyId()
	            );

	    if (tenant == null) {

	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                "CompanyId invalid - "
	                        + activity.getCompanyId()
	        );
	    }

	    Lead lead =
	            leadRepo.findByIdAndCompanyId(
	                    activity.getLeadId(),
	                    activity.getCompanyId()
	            );

	    if (lead == null) {

	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                "Lead not found with id : "
	                        + activity.getLeadId()
	        );
	    }

	    if (activity.getId() != null) {

	        Activity existing =
	                repo.findByIdAndCompanyId(
	                        activity.getId(),
	                        activity.getCompanyId()
	                );

	        if (existing == null) {

	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                    "Activity not found"
	            );
	        }

	        activity.setCreateDate(
	                existing.getCreateDate()
	        );

	        activity.setUpdateDate(
	                new Date()
	        );

	        mapper.map(
	                activity,
	                existing
	        );

	        return repo.save(existing);
	    }

	    activity.setCreateDate(
	            new Date()
	    );

	    activity.setUpdateDate(
	            new Date()
	    );

	    return repo.save(activity);
	}

	@Override
	public Activity getById(
	        Long id,
	        String companyId
	) {

	    Activity activity =
	            repo.findByIdAndCompanyId(
	                    id,
	                    companyId
	            );

	    if (activity == null) {

	        throw new RuntimeException(
	                "Activity not found"
	        );
	    }

	    return activity;
	}

	@Override
	public void delete(
	        Long id,
	        String companyId
	) {

	    Activity activity =
	            repo.findByIdAndCompanyId(
	                    id,
	                    companyId
	            );

	    if (activity == null) {

	        throw new RuntimeException(
	                "Activity not found"
	        );
	    }

	    repo.delete(activity);
	}

	@Override
	public PaginatedResponseDto getPaginated(
	        String companyId,
	        Long leadId,
	        String header,
	        ActionType actionType,
	        String search,
	        LocalDate fromDate,
	        LocalDate toDate,
	        int page,
	        int size
	) {

	    Pageable pageable =
	            PageRequest.of(
	                    page,
	                    size,
	                    Sort.by("createDate")
	                            .descending()
	            );

	    Page<Activity> resultPage =
	            repo.filter(
	                    companyId,
	                    leadId,
	                    header,
	                    actionType,
	                    search,
	                    fromDate == null
	                            ? null
	                            : java.sql.Date.valueOf(fromDate),
	                    toDate == null
	                            ? null
	                            : java.sql.Date.valueOf(toDate),
	                    pageable
	            );

	    PaginatedResponseDto dto =
	            new PaginatedResponseDto();

	    dto.setSelectedPage(page);
	    dto.setTotalNumberOfRecords(
	            (int) resultPage.getTotalElements()
	    );

	    dto.setTotalNumberOfPages(
	            resultPage.getTotalPages()
	    );

	    dto.setRecordsFrom(
	            page * size + 1
	    );

	    dto.setRecordsTo(
	            Math.min(
	                    (page + 1) * size,
	                    (int) resultPage.getTotalElements()
	            )
	    );

	    dto.setList(
	            resultPage.getContent()
	    );

	    return dto;
	}

}

package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.AssignLead;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.AssignLeadRepo;
import com.letswork.crm.repo.LeadRepo;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.service.AssignLeadService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class AssignLeadServiceImpl implements AssignLeadService{
	
	@Autowired
	private LeadRepo leadRepo;

	@Autowired
	private TenantService tenantService;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private AssignLeadRepo repo;
	
	private ModelMapper mapper =
	        new ModelMapper();

	@Override
	public AssignLead createAssign(AssignLead assignLead) {
		
		Tenant tenant =
	            tenantService.findTenantByCompanyId(
	                    assignLead.getCompanyId()
	            );

	    if (tenant == null) {

	        throw new RuntimeException(
	                "CompanyId invalid - "
	                        + assignLead.getCompanyId()
	        );
	    }

	    Lead lead =
	            leadRepo.findByIdAndCompanyId(
	                    assignLead.getLeadId(),
	                    assignLead.getCompanyId()
	            );

	    if (lead == null) {

	        throw new RuntimeException(
	                "Lead not found"
	        );
	    }

	    userRepo.findById(
	            assignLead.getUserId()
	    ).orElseThrow(() ->
	            new RuntimeException(
	                    "User not found"
	            )
	    );

	    if (assignLead.getId() != null) {

	        AssignLead existing =
	                repo.findByIdAndCompanyId(
	                        assignLead.getId(),
	                        assignLead.getCompanyId()
	                ).orElseThrow(() ->
	                        new RuntimeException(
	                                "AssignLead not found"
	                        )
	                );

	        assignLead.setCreateDate(
	                existing.getCreateDate()
	        );

	        assignLead.setUpdateDate(
	                new Date()
	        );

	        mapper.map(
	                assignLead,
	                existing
	        );

	        return repo.save(existing);
	    }

	    assignLead.setCreateDate(
	            new Date()
	    );

	    assignLead.setUpdateDate(
	            new Date()
	    );

	    return repo.save(assignLead);
		
	}

	@Override
	public String bulkAssign(String companyId, Long userId, List<Long> leadIds) {
		
				userRepo.findById(userId)
		        .orElseThrow(() ->
		                new RuntimeException(
		                        "User not found"
		                )
		        );
		
		for (Long leadId : leadIds) {
		
		    Lead lead =
		            leadRepo.findByIdAndCompanyId(
		                    leadId,
		                    companyId
		            );
		
		    if (lead == null) {
		
		        throw new RuntimeException(
		                "Lead not found : "
		                        + leadId
		        );
		    }
		
		    AssignLead existing =
		            repo.findByLeadIdAndCompanyId(
		                    leadId,
		                    companyId
		            );
		
		    if (existing != null) {
		
		        existing.setUserId(userId);
		        existing.setUpdateDate(new Date());
		
		        repo.save(existing);
		    }
		    else {
		
		        AssignLead assign =
		                new AssignLead();
		
		        assign.setCompanyId(companyId);
		        assign.setLeadId(leadId);
		        assign.setUserId(userId);
		        assign.setCreateDate(new Date());
		        assign.setUpdateDate(new Date());
		
		        repo.save(assign);
		    }
		}
		
		return "Leads assigned successfully";
		
	}

	@Override
	public AssignLead getById(Long id, String companyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Long id, String companyId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PaginatedResponseDto getPaginated(
	        String companyId,
	        Long leadId,
	        Long userId,
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

	    Page<AssignLead> resultPage =
	            repo.filter(
	                    companyId,
	                    leadId,
	                    userId,
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

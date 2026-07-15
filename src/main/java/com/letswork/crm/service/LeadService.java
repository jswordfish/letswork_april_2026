package com.letswork.crm.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.letswork.crm.dtos.LeadResponseDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.enums.LeadQuality;
import com.letswork.crm.enums.LeadStatus;
import com.letswork.crm.enums.Source;

public interface LeadService {
	
	Lead saveOrUpdate(Lead lead);

	public PaginatedResponseDto getPaginated(
	        String companyId,
	        String name,
	        String email,
	        String phone,
	        String clientCompanyName,
	        List<Source> sources,
	        String location,
	        List<LeadStatus> statuses,
	        List<LeadQuality> leadQualities,
	        List<String> letsWorkCentres,
	        String city,
	        String state,
	        String solution,
	        String search,
	        Long userId,
	        LocalDate fromDate,
	        LocalDate toDate,
	        int page,
	        int size
	);
	
	void exportToExcel(
			String companyId,
	        String name,
	        String email,
	        String phone,
	        String clientCompanyName,
	        List<Source> sources,
	        String location,
	        List<LeadStatus> statuses,
	        List<LeadQuality> leadQualities,
	        List<String> letsWorkCentres,
	        String city,
	        String state,
	        String solution,
	        String search,
	        Long userId,
	        LocalDate fromDate,
	        LocalDate toDate,
	        HttpServletResponse response
	) throws IOException;
    
    Lead changeStatus(
            Long leadId,
            String companyId,
            LeadStatus status
    );

    void delete(Long id, String companyId);

    LeadResponseDto getById(Long id, String companyId);
    
}

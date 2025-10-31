package com.letswork.crm.service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ClientCompanySeatMapping;

public interface ClientCompanySeatMappingService {
	
	public ClientCompanySeatMapping saveOrUpdate(ClientCompanySeatMapping mapping);
	
	public PaginatedResponseDto listByLetsWorkCentre(String companyId, String letsWorkCentre, String city, String state, int page);
	
	public PaginatedResponseDto listForSpecificClient(String clientCompanyName, String letsWorkCentre,
            String companyId, String city, String state, int page);
	
	public String deleteMapping(Long id);

}

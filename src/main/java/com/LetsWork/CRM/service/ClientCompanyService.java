package com.LetsWork.CRM.service;

import java.util.List;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.ClientCompany;


public interface ClientCompanyService {
	
	String saveOrUpdate(ClientCompany clientCompany);
	
	List<ClientCompany> findByIndustry(String industry);
	
	List<ClientCompany> listAll();
	
	PaginatedResponseDto listAll(int page);
	
	String deleteCompany(ClientCompany clientCompany);
	
	
	List<ClientCompany> getClientCompaniesByLocation(String location, String companyId);
	
	PaginatedResponseDto getClientCompaniesByLocation(String location, String companyId, int page);

}

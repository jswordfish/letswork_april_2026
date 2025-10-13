package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ClientCompany;


public interface ClientCompanyService {
	
	String saveOrUpdate(ClientCompany clientCompany);
	
	List<ClientCompany> findByIndustry(String industry);
	
	List<ClientCompany> listAll();
	
	PaginatedResponseDto listAll(int page);
	
	String deleteCompany(ClientCompany clientCompany);
	
	
	List<ClientCompany> getClientCompaniesByLocation(String location, String companyId);
	
	PaginatedResponseDto getClientCompaniesByLocation(String location, String companyId, int page);
	
	public List<String> uploadClientCompanies(MultipartFile file, String companyId) throws IOException;

}

package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ClientCompany;


public interface ClientCompanyService {
	
	String saveOrUpdate(ClientCompany clientCompany);
	
	
	
	List<ClientCompany> listAll();
	
	PaginatedResponseDto listAll(int page);
	
	String deleteCompany(ClientCompany clientCompany);
	
	
	List<ClientCompany> getClientCompaniesByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state);
	
	PaginatedResponseDto getClientCompaniesByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page);
	
	public String uploadClientCompanies(MultipartFile file) throws IOException;
	
	public PaginatedResponseDto getClientCompanies(String companyId, int page);

}

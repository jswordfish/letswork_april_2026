package com.letswork.crm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.LetsworkUser;




public interface LetsworkUserService {
	
	String saveOrUpdate(LetsworkUser client);
	
	LetsworkUser getByEmail(String email, String companyId);
	
	String deleteClient(LetsworkUser client);
	
	List<LetsworkUser> getClientsByCompany(String companyName, String companyId);
	
	PaginatedResponseDto getClientsByCompany(String companyName, String companyId, int page);
	
	List<LetsworkUser> getIndividualClients(String companyId);
	
	List<LetsworkUser> getIndividualClientsByLetsWorkCentre(String letsWorkCentre, String companyId);
		
	PaginatedResponseDto getIndividualClients(String companyId, int page);
	
	PaginatedResponseDto getIndividualClientsByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page);
	
	public String uploadClientsFromExcel(MultipartFile file);
	
	public PaginatedResponseDto getClients(String companyId, String email, String letsWorkCentre, String city,
            String state, String search, String sort,
            int pageNo, int pageSize);

}

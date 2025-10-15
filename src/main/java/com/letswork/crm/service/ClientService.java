package com.letswork.crm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Client;




public interface ClientService {
	
	String saveOrUpdate(Client client);
	
	
	List<Client> findByName(String name);
	
	String deleteClient(Client client);
	
	List<Client> getClientsByCompany(String companyName, String companyId);
	
	PaginatedResponseDto getClientsByCompany(String companyName, String companyId, int page);
	
	List<Client> getIndividualClients(String companyId);
	
	List<Client> getIndividualClientsByLetsWorkCentre(String letsWorkCentre, String companyId);
	
	PaginatedResponseDto findByName(String name, int page);
	
	PaginatedResponseDto getIndividualClients(String companyId, int page);
	
	PaginatedResponseDto getIndividualClientsByLetsWorkCentre(String letsWorkCentre, String companyId, int page);
	
	public String uploadClientsFromExcel(MultipartFile file);

}

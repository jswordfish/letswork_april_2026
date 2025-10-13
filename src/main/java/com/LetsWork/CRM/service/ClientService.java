package com.LetsWork.CRM.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.Client;


public interface ClientService {
	
	String saveOrUpdate(Client client);
	
	
	List<Client> findByName(String name);
	
	String deleteClient(Client client);
	
	List<Client> getClientsByCompany(String companyName, String companyId);
	
	PaginatedResponseDto getClientsByCompany(String companyName, String companyId, int page);
	
	List<Client> getIndividualClients(String companyId);
	
	List<Client> getIndividualClientsByLocation(String location, String companyId);
	
	PaginatedResponseDto findByName(String name, int page);
	
	PaginatedResponseDto getIndividualClients(String companyId, int page);
	
	PaginatedResponseDto getIndividualClientsByLocation(String location, String companyId, int page);
	
	public String uploadClientsFromExcel(MultipartFile file);

}

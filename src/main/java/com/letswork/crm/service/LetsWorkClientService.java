package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.LetsWorkClientPurchesedDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.UserWithCompaniesDto;
import com.letswork.crm.entities.LetsWorkClient;


public interface LetsWorkClientService {
	
	public String saveOrUpdate(
	        LetsWorkClient clientCompany,
	        MultipartFile aadhaar,
	        MultipartFile pan,
	        MultipartFile tan,
	        MultipartFile gst
	) throws IOException;
	
	UserWithCompaniesDto getUserWithCompanies(
            Long userId,
            String companyId
    );
	
	PaginatedResponseDto listClientCompanies(
	        String companyId,
	        String letsWorkCentre,
	        String city,
	        String state,
	        String category,
	        String subCategory,
	        String search,
	        String sort,
	        int page,
	        int size
	);
	
	List<LetsWorkClient> listAll();
	
	PaginatedResponseDto listAll(int page);
	
	String deleteCompany(LetsWorkClient clientCompany);
	
	
	List<LetsWorkClient> getClientCompaniesByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state);
	
	PaginatedResponseDto getClientCompaniesByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page);
	
	public String uploadClientCompanies(MultipartFile file) throws IOException;
	
	public PaginatedResponseDto getClientCompanies(String companyId, int page);
	
	public void updateDayPass(String numberOfDays, String email, String companyId);
	
	public void updateConferenceCredits(
	        String numberOfHours,
	        String email,
	        String companyId
	);
	
	public LetsWorkClientPurchesedDto  getLetsWorkClientPurchesed(
			Long clientId);

}

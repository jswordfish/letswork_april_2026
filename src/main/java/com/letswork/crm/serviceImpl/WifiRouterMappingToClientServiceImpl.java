package com.letswork.crm.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Client;
import com.letswork.crm.entities.WifiRouter;
import com.letswork.crm.entities.WifiRouterMappingToClient;
import com.letswork.crm.repo.ClientRepository;
import com.letswork.crm.repo.WifiRouterMappingToClientRepository;
import com.letswork.crm.repo.WifiRouterRepository;
import com.letswork.crm.service.WifiRouterMappingToClientService;




@Service
@Transactional
public class WifiRouterMappingToClientServiceImpl implements WifiRouterMappingToClientService {
	
	@Autowired
	WifiRouterMappingToClientRepository repo;
	
	@Autowired
	WifiRouterRepository wifiRouterRepo;
	
	@Autowired
	ClientRepository clientRepo;

	@Override
	public String saveOrUpdate(WifiRouterMappingToClient mapping) {
		// TODO Auto-generated method stub
		
		WifiRouter wifi = wifiRouterRepo.findByNameLetsWorkCentreAndCompany(mapping.getWifiName(), mapping.getLetsWorkCentre(), mapping.getCompanyId());
		
		if(wifi==null) {
			return "Wifi router with name "+mapping.getWifiName()+" does not exists";
		}
		
		Client client = clientRepo.findByEmailAndCompanyId( mapping.getClientEmail(), mapping.getCompanyId());
		
		if(client==null) {
			return "Client with name "+mapping.getClientName()+" does not exists";
		}
		
		WifiRouterMappingToClient mapping2 = repo.findByWifiNameAndClientEmailAndLetsWorkCentre(mapping.getWifiName(), mapping.getClientEmail(), mapping.getLetsWorkCentre());
		
		if(mapping2!=null) {
			
			return "Mapping already exists";
			
		}
		else {
			repo.save(mapping);
			return "Mapping saved";
		}
		
	}

	@Override
	public String deleteMapping(WifiRouterMappingToClient mapping) {
		// TODO Auto-generated method stub
		
		WifiRouterMappingToClient mapping2 = repo.findByWifiNameAndClientEmailAndLetsWorkCentre(mapping.getWifiName(), mapping.getClientEmail(), mapping.getLetsWorkCentre());
				
		if(mapping2!=null) {
			
			repo.delete(mapping2);
			return "Mapping deleted";
			
		}
		else {
			
			return "Mapping does not exist";
		}
		
	}
	
	private static final int PAGE_SIZE = 10;

	@Override
	public PaginatedResponseDto getClientsByWifi(String wifiName, String letsWorkCentre, String companyId, int page) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Client> resultPage = repo.findClientsByWifi(wifiName, letsWorkCentre, companyId, pageable);
        return buildPaginatedResponse(resultPage, page);
	}

	@Override
	public PaginatedResponseDto getRoutersByClient(String clientName, String clientEmail, String companyId, int page) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<WifiRouter> resultPage = repo.findRoutersByClient( clientEmail, companyId, pageable);
        return buildPaginatedResponse(resultPage, page);
	}
	
	private PaginatedResponseDto buildPaginatedResponse(Page<?> pageData, int page) {
        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setRecordsFrom((page * PAGE_SIZE) + 1);
        dto.setRecordsTo((page * PAGE_SIZE) + pageData.getNumberOfElements());
        dto.setTotalNumberOfRecords((int) pageData.getTotalElements());
        dto.setTotalNumberOfPages(pageData.getTotalPages());
        dto.setSelectedPage(page);
        dto.setList(pageData.getContent());
        return dto;
    }
	
	

}

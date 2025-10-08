package com.LetsWork.CRM.service;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.WifiRouterMappingToClient;

public interface WifiRouterMappingToClientService {
	
	public String saveOrUpdate(WifiRouterMappingToClient mapping);
	
	PaginatedResponseDto getClientsByWifi(String wifiName, String location, String companyId, int page);

    PaginatedResponseDto getRoutersByClient(String clientName, String clientEmail, String companyId, int page);
	
	public String deleteMapping(WifiRouterMappingToClient mapping);

}

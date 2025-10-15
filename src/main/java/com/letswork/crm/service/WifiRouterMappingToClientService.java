package com.letswork.crm.service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.WifiRouterMappingToClient;

public interface WifiRouterMappingToClientService {
	
	public String saveOrUpdate(WifiRouterMappingToClient mapping);
	
	PaginatedResponseDto getClientsByWifi(String wifiName, String letsWorkCentre, String companyId, int page);

    PaginatedResponseDto getRoutersByClient(String clientName, String clientEmail, String companyId, int page);
	
	public String deleteMapping(WifiRouterMappingToClient mapping);

}

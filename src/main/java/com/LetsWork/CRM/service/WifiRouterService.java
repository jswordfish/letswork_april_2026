package com.LetsWork.CRM.service;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.WifiRouter;


public interface WifiRouterService {
	
	String saveOrUpdate(WifiRouter wifiRouter);

    PaginatedResponseDto listByLocation(String location, String companyId, int page);

    String deleteWifiRouter(WifiRouter wifiRouter);

}

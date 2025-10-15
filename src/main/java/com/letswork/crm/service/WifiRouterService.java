package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.WifiRouter;


public interface WifiRouterService {
	
	String saveOrUpdate(WifiRouter wifiRouter);

    PaginatedResponseDto listByLetsWorkCentre(String letsWorkCentre, String companyId, int page);

    String deleteWifiRouter(WifiRouter wifiRouter);
    
    public String uploadWifiRouters(MultipartFile file) throws IOException;

}

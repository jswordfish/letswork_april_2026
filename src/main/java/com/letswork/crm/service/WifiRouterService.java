package com.LetsWork.CRM.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.WifiRouter;


public interface WifiRouterService {
	
	String saveOrUpdate(WifiRouter wifiRouter);

    PaginatedResponseDto listByLocation(String location, String companyId, int page);

    String deleteWifiRouter(WifiRouter wifiRouter);
    
    public List<String> uploadWifiRouters(MultipartFile file, String companyId) throws IOException;

}

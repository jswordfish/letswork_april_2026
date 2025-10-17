package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Holiday;

public interface HolidayService {
	
	String saveOrUpdate(Holiday holiday);


    String uploadHolidays(MultipartFile file) throws IOException;
    
    PaginatedResponseDto listHolidays(String companyId, String letsWorkCentre, String city, String state, int page, int size);

}

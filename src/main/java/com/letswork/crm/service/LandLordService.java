package com.letswork.crm.service;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.LandLord;

public interface LandLordService {
	
	LandLord saveOrUpdate(LandLord landLord,
            MultipartFile panFile,
            MultipartFile aadharFile,
            MultipartFile gstFile,
            MultipartFile agreementFile) throws Exception;

	PaginatedResponseDto listAll(int page, String companyId);
	
	void deleteById(Long id);
	
	Optional<LandLord> findByGstNumber(String gstNumber);

}

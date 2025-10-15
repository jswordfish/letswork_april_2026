package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Cabin;

public interface CabinService {
	
	Cabin saveOrUpdate(Cabin cabin);

    PaginatedResponseDto listAll(String companyId, int page, int size);

    void delete(Long id);

    String uploadCabins(MultipartFile file) throws IOException;

}

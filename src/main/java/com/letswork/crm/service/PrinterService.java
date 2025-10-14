package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Printer;

public interface PrinterService {
	
	Printer saveOrUpdate(Printer printer);

    PaginatedResponseDto listPrinters(String companyId, int page, int size);

    void deletePrinter(Long id);

    List<String> uploadPrintersFromExcel(MultipartFile file) throws IOException;

}

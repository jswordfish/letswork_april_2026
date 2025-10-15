package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.PrinterExcelDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Printer;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.PrinterType;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.PrinterRepository;
import com.letswork.crm.service.PrinterService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PrinterServiceImpl implements PrinterService {
	
	@Autowired
    private PrinterRepository repo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;

    @Override
    public Printer saveOrUpdate(Printer printer) {
    	
		Tenant tenant = tenantService.findTenantByCompanyId(printer.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+printer.getCompanyId());
			
		}
		
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyId(printer.getLetsWorkCentre(), printer.getCompanyId());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}
    	
        Printer existing = repo.findByPrinterNameAndLetsWorkCentreAndCompanyId(
                printer.getPrinterName(), printer.getLetsWorkCentre(), printer.getCompanyId());

        if (existing != null) {
            existing.setPrinterType(printer.getPrinterType());
            existing.setPrinterCompany(printer.getPrinterCompany());
            return repo.save(existing);
        } else {
            return repo.save(printer);
        }
    }

    @Override
    public PaginatedResponseDto listPrinters(String companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Printer> printerPage = repo.findAllByCompanyId(companyId, pageable);

        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setRecordsFrom((page - 1) * size + 1);
        dto.setRecordsTo(Math.min(page * size, (int) printerPage.getTotalElements()));
        dto.setTotalNumberOfRecords((int) printerPage.getTotalElements());
        dto.setTotalNumberOfPages(printerPage.getTotalPages());
        dto.setSelectedPage(page);
        dto.setList(printerPage.getContent());
        return dto;
    }

    @Override
    public void deletePrinter(Long id) {
        repo.deleteById(id);
    }
    
    
    private String validate(PrinterExcelDto dto) {
		if(dto.getPrinterName() == null || dto.getPrinterName().length() == 0) {
			return "Printer Name Should not be null";
		}
		
		if(dto.getPrinterType() == null || dto.getPrinterType().length() == 0) {
			return "Printer Type Should not be null";		
			}
		
		if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
			return "LetsWork Centre Should not be null";	
			}
		
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId Should not be null";	
			}
		
		if(dto.getPrinterCompany() == null || dto.getPrinterCompany().length() == 0) {
			return "Printer Company Should not be null";		
			}
		
		
		return "ok";
	}
    

    @Override
    public String uploadPrintersFromExcel(MultipartFile file) throws IOException {
        List<PrinterExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, PrinterExcelDto.class);
        
        for(PrinterExcelDto dto : dtos) {
        	String val = validate(dto);
    		if(!val.equalsIgnoreCase("ok")) {
    			return val;
    		}
    	}
        
        List<String> responses = new ArrayList<>();

        for (PrinterExcelDto dto : dtos) {
            try {
                if (!StringUtils.hasText(dto.getPrinterName()) || !StringUtils.hasText(dto.getLetsWorkCentre())) {
                    responses.add("Skipped: Missing required fields.");
                    continue;
                }

                Printer printer = Printer.builder()
                        .printerName(dto.getPrinterName().trim())
                        .letsWorkCentre(dto.getLetsWorkCentre().trim())
                        .printerCompany(dto.getPrinterCompany().trim())
                        .printerType(PrinterType.valueOf(dto.getPrinterType().toUpperCase()))
                        .companyId(dto.getCompanyId().trim())
                        .build();

                saveOrUpdate(printer);
                responses.add("Saved/Updated: " + printer.getPrinterName());
            } catch (Exception e) {
                responses.add("Error saving " + dto.getPrinterName() + ": " + e.getMessage());
            }
        }
        return "ok";
    }
}

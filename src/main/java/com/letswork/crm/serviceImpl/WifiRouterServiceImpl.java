package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.WifiRouterExcelDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.entities.WifiRouter;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.WifiRouterRepository;
import com.letswork.crm.service.TenantService;
import com.letswork.crm.service.WifiRouterService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;



@Service
@Transactional
public class WifiRouterServiceImpl implements WifiRouterService {
	
	@Autowired
	WifiRouterRepository repo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	private static final int PAGE_SIZE = 10;

	@Override
	public String saveOrUpdate(WifiRouter wifiRouter) {
		// TODO Auto-generated method stub
		
		Tenant tenant = tenantService.findTenantByCompanyId(wifiRouter.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+wifiRouter.getCompanyId());
			
		}


		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyId(wifiRouter.getLetsWorkCentre(), wifiRouter.getCompanyId());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}
		
		
		WifiRouter wifi = repo.findByNameLetsWorkCentreAndCompany(wifiRouter.getWifiName(), wifiRouter.getLetsWorkCentre(), wifiRouter.getCompanyId());
		
		if(wifi!=null) {
			
			wifi.setPassword(wifiRouter.getPassword());
			
			
			repo.save(wifi);
			return "record updated";
			
		}
		
		else {
			repo.save(wifiRouter);
			return "record saved";
		}
	}
	
	//Null validation
	private String validate(WifiRouterExcelDto dto) {
		if(dto.getWifiName() == null || dto.getWifiName().length() == 0) {
			return "Wifi Name Should not be null";
		}
		
		if(dto.getPassword() == null || dto.getPassword().length() == 0) {
			return "Password Should not be null";		
			}
		
		if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
			return "LetsWork Centre Should not be null";	
			}
		
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId Should not be null";	
			}
		
		
		return "ok";
	}

	
	
	@Override
	public String uploadWifiRouters(MultipartFile file) throws IOException {
	    List<WifiRouterExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, WifiRouterExcelDto.class);
	    
	    for(WifiRouterExcelDto dto : dtos) {
    		String val = validate(dto);
    		if(!val.equalsIgnoreCase("ok")) {
    			return val;
    		}
    	}
	    
	    List<String> responses = new ArrayList<>();

	    for (WifiRouterExcelDto dto : dtos) {
	        try {
	            WifiRouter router = WifiRouter.builder()
	                    .letsWorkCentre(dto.getLetsWorkCentre().trim())
	                    .wifiName(dto.getWifiName().trim())
	                    .password(dto.getPassword().trim())
	                    .companyId(dto.getCompanyId().trim())
	                    .build();

	            String result = saveOrUpdate(router); 

	            responses.add(result + router.getWifiName());
	        } catch (Exception e) {
	            responses.add("Error saving " + dto.getWifiName() + ": " + e.getMessage());
	        }
	    }
	    return "ok";
	}

	@Override
	public PaginatedResponseDto listByLetsWorkCentre(String letsWorkCentre, String companyId, int page) {
		// TODO Auto-generated method stub
		Page<WifiRouter> wifiPage = repo.findByLetsWorkCentre(letsWorkCentre, companyId, PageRequest.of(page, PAGE_SIZE));

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((int) wifiPage.getPageable().getOffset() + 1);
        response.setRecordsTo((int) wifiPage.getPageable().getOffset() + wifiPage.getNumberOfElements());
        response.setTotalNumberOfRecords((int) wifiPage.getTotalElements());
        response.setTotalNumberOfPages(wifiPage.getTotalPages());
        response.setSelectedPage(page);
        response.setList(wifiPage.getContent());

        return response;
	}

	@Override
	public String deleteWifiRouter(WifiRouter wifiRouter) {
		// TODO Auto-generated method stub
		
		WifiRouter wifi = repo.findByNameLetsWorkCentreAndCompany(wifiRouter.getWifiName(), wifiRouter.getLetsWorkCentre(), wifiRouter.getCompanyId());
		
		if(wifi!=null) {
			
			repo.delete(wifi);
			return "record deleted";
			
		}
		
		else {
			
			return "No record found";
		}
		
	}

}

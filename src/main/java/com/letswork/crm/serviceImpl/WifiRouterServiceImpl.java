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
import com.letswork.crm.entities.WifiRouter;
import com.letswork.crm.repo.WifiRouterRepository;
import com.letswork.crm.service.WifiRouterService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;



@Service
@Transactional
public class WifiRouterServiceImpl implements WifiRouterService {
	
	@Autowired
	WifiRouterRepository repo;
	
	private static final int PAGE_SIZE = 10;

	@Override
	public String saveOrUpdate(WifiRouter wifiRouter) {
		// TODO Auto-generated method stub
		WifiRouter wifi = repo.findByNameLocationAndCompany(wifiRouter.getWifiName(), wifiRouter.getLocation(), wifiRouter.getCompanyId());
		
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
	
	@Override
	public List<String> uploadWifiRouters(MultipartFile file, String companyId) throws IOException {
	    List<WifiRouterExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, WifiRouterExcelDto.class);
	    List<String> responses = new ArrayList<>();

	    for (WifiRouterExcelDto dto : dtos) {
	        try {
	            WifiRouter router = WifiRouter.builder()
	                    .location(dto.getLocation())
	                    .wifiName(dto.getWifiName())
	                    .password(dto.getPassword())
	                    .build();
	            router.setCompanyId(companyId);

	            String result = saveOrUpdate(router); 

	            responses.add(result + router.getWifiName());
	        } catch (Exception e) {
	            responses.add("Error saving " + dto.getWifiName() + ": " + e.getMessage());
	        }
	    }
	    return responses;
	}

	@Override
	public PaginatedResponseDto listByLocation(String location, String companyId, int page) {
		// TODO Auto-generated method stub
		Page<WifiRouter> wifiPage = repo.findByLocation(location, companyId, PageRequest.of(page, PAGE_SIZE));

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
		
		WifiRouter wifi = repo.findByNameLocationAndCompany(wifiRouter.getWifiName(), wifiRouter.getLocation(), wifiRouter.getCompanyId());
		
		if(wifi!=null) {
			
			repo.delete(wifi);
			return "record deleted";
			
		}
		
		else {
			
			return "No record found";
		}
		
	}

}

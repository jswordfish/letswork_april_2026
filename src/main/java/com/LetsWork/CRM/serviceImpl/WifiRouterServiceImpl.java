package com.LetsWork.CRM.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.WifiRouter;
import com.LetsWork.CRM.repo.WifiRouterRepository;
import com.LetsWork.CRM.service.WifiRouterService;



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

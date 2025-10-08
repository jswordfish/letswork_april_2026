package com.LetsWork.CRM.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.ParkingSlot;
import com.LetsWork.CRM.repo.ParkingSlotRepository;
import com.LetsWork.CRM.service.ParkingSlotService;


@Service
public class ParkingSlotServiceImpl implements ParkingSlotService {
	
	@Autowired
	ParkingSlotRepository repo;	
	
	private static final int PAGE_SIZE = 10;

	@Override
	public String saveOrUpdate(ParkingSlot parkingSlot) {
		// TODO Auto-generated method stub
		
		ParkingSlot slot = repo.findByNameLocationAndCompany(parkingSlot.getName(), parkingSlot.getLocation(), parkingSlot.getCompanyId());
		
		if(slot!=null) {
			
			slot.setFloorNumber(parkingSlot.getFloorNumber());
			slot.setOtherDetails(parkingSlot.getOtherDetails());
			
			
			repo.save(slot);
			return "record updated";
			
		}
		
		else {
			repo.save(parkingSlot);
			return "record saved";
		}
	}

	@Override
	public PaginatedResponseDto listByLocation(String location, String companyId, int page) {
		// TODO Auto-generated method stub
		Page<ParkingSlot> parkingPage = repo.findByLocation(location, companyId, PageRequest.of(page, PAGE_SIZE));

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((int) parkingPage.getPageable().getOffset() + 1);
        response.setRecordsTo((int) parkingPage.getPageable().getOffset() + parkingPage.getNumberOfElements());
        response.setTotalNumberOfRecords((int) parkingPage.getTotalElements());
        response.setTotalNumberOfPages(parkingPage.getTotalPages());
        response.setSelectedPage(page);
        response.setList(parkingPage.getContent());

        return response;
	}

	@Override
	public String deleteParkingSlot(ParkingSlot parkingSlot) {
		// TODO Auto-generated method stub
		
		ParkingSlot slot = repo.findByNameLocationAndCompany(parkingSlot.getName(), parkingSlot.getLocation(), parkingSlot.getCompanyId());
		
		if(slot!=null) {
			
			repo.delete(slot);
			return "record deleted";
			
		}
		
		else {
			
			return "No record found";
		}
		
		
	}

}

package com.LetsWork.CRM.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.dtos.ParkingSlotExcelDto;
import com.LetsWork.CRM.entities.ParkingSlot;
import com.LetsWork.CRM.repo.ParkingSlotRepository;
import com.LetsWork.CRM.service.ParkingSlotService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;


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
	public List<String> uploadParkingSlots(MultipartFile file, String companyId) throws IOException {
	    List<ParkingSlotExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, ParkingSlotExcelDto.class);
	    List<String> responses = new ArrayList<>();

	    for (ParkingSlotExcelDto dto : dtos) {
	        try {
	            ParkingSlot slot = ParkingSlot.builder()
	                    .name(dto.getName())
	                    .location(dto.getLocation())
	                    .floorNumber(dto.getFloorNumber())
	                    .otherDetails(dto.getOtherDetails())
	                    .build();
	            slot.setCompanyId(companyId);

	            String result = saveOrUpdate(slot); 

	            responses.add(result + slot.getName());
	        } catch (Exception e) {
	            responses.add("Error saving " + dto.getName() + ": " + e.getMessage());
	        }
	    }
	    return responses;
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

package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.ParkingSlotExcelDto;
import com.letswork.crm.entities.ParkingSlot;
import com.letswork.crm.repo.ParkingSlotRepository;
import com.letswork.crm.service.ParkingSlotService;
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
		
		ParkingSlot slot = repo.findByNameLetsWorkCentreAndCompany(parkingSlot.getName(), parkingSlot.getLetsWorkCentre(), parkingSlot.getCompanyId());
		
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
	public List<String> uploadParkingSlots(MultipartFile file) throws IOException {
	    List<ParkingSlotExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, ParkingSlotExcelDto.class);
	    List<String> responses = new ArrayList<>();

	    for (ParkingSlotExcelDto dto : dtos) {
	        try {
	            ParkingSlot slot = ParkingSlot.builder()
	                    .name(dto.getName())
	                    .letsWorkCentre(dto.getLetsWorkCentre())
	                    .floorNumber(dto.getFloorNumber())
	                    .otherDetails(dto.getOtherDetails())
	                    .companyId(dto.getCompanyId())
	                    .build();

	            String result = saveOrUpdate(slot); 

	            responses.add(result + slot.getName());
	        } catch (Exception e) {
	            responses.add("Error saving " + dto.getName() + ": " + e.getMessage());
	        }
	    }
	    return responses;
	}

	@Override
	public PaginatedResponseDto listByLetsWorkCentre(String letsWorkCentre, String companyId, int page) {
		// TODO Auto-generated method stub
		Page<ParkingSlot> parkingPage = repo.findByLetsWorkCentre(letsWorkCentre, companyId, PageRequest.of(page, PAGE_SIZE));

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
		
		ParkingSlot slot = repo.findByNameLetsWorkCentreAndCompany(parkingSlot.getName(), parkingSlot.getLetsWorkCentre(), parkingSlot.getCompanyId());
		
		if(slot!=null) {
			
			repo.delete(slot);
			return "record deleted";
			
		}
		
		else {
			
			return "No record found";
		}
		
		
	}

}

package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.ParkingSlotExcelDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.ParkingSlot;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.ParkingSlotRepository;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.ParkingSlotService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;


@Service
public class ParkingSlotServiceImpl implements ParkingSlotService {
	
	@Autowired
	ParkingSlotRepository repo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	@Autowired
	LetsWorkCentreService letsWorkCentreService;
	
	ModelMapper mapper = new ModelMapper();
	
	private static final int PAGE_SIZE = 10;

	@Override
	public String saveOrUpdate(ParkingSlot parkingSlot) {
		// TODO Auto-generated method stub
		
		Tenant tenant = tenantService.findTenantByCompanyId(parkingSlot.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+parkingSlot.getCompanyId());
			
		}


		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyId(parkingSlot.getLetsWorkCentre(), parkingSlot.getCompanyId());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}	

		
		ParkingSlot slot = repo.findByNameLetsWorkCentreAndCompany(parkingSlot.getName(), parkingSlot.getLetsWorkCentre(), parkingSlot.getCompanyId());
		
		if(slot!=null) {
			
//			slot.setFloorNumber(parkingSlot.getFloorNumber());
//			slot.setOtherDetails(parkingSlot.getOtherDetails());
			
			parkingSlot.setId(slot.getId());
			parkingSlot.setUpdateDate(new Date());
			mapper.map(parkingSlot, slot);
			
			repo.save(slot);
			return "record updated";
			
		}
		
		else {
			parkingSlot.setCreateDate(new Date());
			repo.save(parkingSlot);
			return "record saved";
		}
	}
	
	
	private String validate(ParkingSlotExcelDto dto) {
		if(dto.getName() == null || dto.getName().length() == 0) {
			return "Parking name Should not be null";
		}
		
		if(dto.getFloorNumber() == null || dto.getFloorNumber().length() == 0) {
			return "Floor number Should not be null";		
			}
		
		if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
			return "LetsWork Centre Should not be null";	
			}
		
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId Should not be null";	
			}
		
		if(dto.getOtherDetails() == null || dto.getOtherDetails().length() == 0) {
			return "Other Details Should not be null";	
			}
		
		if(letsWorkCentreService.findByName(dto.getLetsWorkCentre(), dto.getCompanyId()) == null){
			return "Letswork Cente "+dto.getLetsWorkCentre()+" does not exist";
		}
		
		if(tenantService.findTenantByCompanyId(dto.getCompanyId())==null) {
			return "CompanyId "+dto.getCompanyId()+" does not exists";
		}
		
		
		return "ok";
	}
	
	
	@Override
	public String uploadParkingSlots(MultipartFile file) throws IOException {
	    List<ParkingSlotExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, ParkingSlotExcelDto.class);
	    
	    for(ParkingSlotExcelDto dto : dtos) {
	    	String val = validate(dto);
    		if(!val.equalsIgnoreCase("ok")) {
    			return val;
    		}
    	}
	    
	    List<String> responses = new ArrayList<>();

	    for (ParkingSlotExcelDto dto : dtos) {
	        try {
	            ParkingSlot slot = ParkingSlot.builder()
	                    .name(dto.getName().trim())
	                    .letsWorkCentre(dto.getLetsWorkCentre().trim())
	                    .floorNumber(dto.getFloorNumber().trim())
	                    .otherDetails(dto.getOtherDetails().trim())
	                    .companyId(dto.getCompanyId().trim())
	                    .build();

	            String result = saveOrUpdate(slot); 

	            responses.add(result + slot.getName());
	        } catch (Exception e) {
	            responses.add("Error saving " + dto.getName() + ": " + e.getMessage());
	        }
	    }
	    return "ok";
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

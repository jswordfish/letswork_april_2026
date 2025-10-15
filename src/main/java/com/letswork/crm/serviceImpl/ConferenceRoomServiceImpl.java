package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.ConferenceRoomExcelDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.ConferenceRoomService;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;



@Service
@Transactional
public class ConferenceRoomServiceImpl implements ConferenceRoomService {
	
	@Autowired
	ConferenceRoomRepository repo;
	
	@Autowired
	LetsWorkCentreService letsWorkCentreService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	@Autowired
	TenantService tenantService;

	@Override
	public String saveOrUpdate(ConferenceRoom conferenceRoom) {
		// TODO Auto-generated method stub
		
		Tenant tenant = tenantService.findTenantByCompanyId(conferenceRoom.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+conferenceRoom.getCompanyId());
			
		}
		
		ConferenceRoom room = repo.findByNameAndLetsWorkCentreAndCompanyId(conferenceRoom.getName(), conferenceRoom.getLetsWorkCentre(), conferenceRoom.getCompanyId());
		
		if(room!=null) {
			
			room.setName(conferenceRoom.getName());
			room.setCapacity(conferenceRoom.getCapacity());
			room.setLetsWorkCentre(conferenceRoom.getLetsWorkCentre());
			room.setHasProjector(conferenceRoom.isHasProjector());
			room.setHasWhiteBoard(conferenceRoom.isHasWhiteBoard());
			room.setHasChargingPorts(conferenceRoom.isHasChargingPorts());
			
			
			repo.save(room);
			return "record updated";
			
		}
		
		else {
			repo.save(conferenceRoom);
			return "record saved";
		}
		
	}
	
	@Override
	public List<String> uploadConferenceRooms(MultipartFile file) throws IOException {
	    List<ConferenceRoomExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, ConferenceRoomExcelDto.class);

	    List<String> responses = new ArrayList<>();

	    for (ConferenceRoomExcelDto dto : dtos) {
	        try {
	            ConferenceRoom room = ConferenceRoom.builder()
	                    .name(dto.getName())
	                    .capacity(dto.getCapacity())
	                    .letsWorkCentre(dto.getLetsWorkCentre())
	                    .companyId(dto.getCompanyId())
	                    .hasProjector(dto.isHasProjector())
	                    .hasWhiteBoard(dto.isHasWhiteBoard())
	                    .hasChargingPorts(dto.isHasChargingPorts())
	                    .build();

	            

	           
	            String result = saveOrUpdate(room);

	            responses.add(result + ": " + room.getName());
	        } catch (Exception e) {
	            responses.add("Error saving " + dto.getName() + ": " + e.getMessage());
	        }
	    }

	    return responses;
	}
		
	

	@Override
	public List<ConferenceRoom> findByLetsWorkCentre(String letsWorkCentre, String companyId) {
		// TODO Auto-generated method stub
		
		LetsWorkCentre loc = letsWorkCentreRepo.findByNameAndCompanyId(letsWorkCentre, companyId);

	    if (loc == null) {
	        return new ArrayList<>();
	    }

	    
	    return repo.findByLetsWorkCentreAndCompanyId(loc.getName(), companyId);
	}

	@Override
	public String deleteByName(ConferenceRoom conferenceRoom) {
		// TODO Auto-generated method stub
		
		ConferenceRoom room = repo.findByNameAndLetsWorkCentreAndCompanyId(conferenceRoom.getName(), conferenceRoom.getLetsWorkCentre(), conferenceRoom.getCompanyId());
		
		if(room!=null) {
			repo.delete(room);
			return "record deleted";
		}
		else return "record not found";
		
	}
	
	private static final int PAGE_SIZE = 10; 

    
    @Override
    public PaginatedResponseDto findByLetsWorkCentre(String letsWorkCentre, String companyId, int page) {
        // Check if letsWorkCentre exists
    	LetsWorkCentre loc = letsWorkCentreRepo.findByNameAndCompanyId(letsWorkCentre, companyId);
        if (loc == null) {
            return new PaginatedResponseDto(); 
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        Page<ConferenceRoom> roomPage = repo.findByLetsWorkCentreAndCompanyId(loc.getName(), companyId, pageable);

        return buildPaginatedResponse(roomPage, page);
    }

    
//    @Override
//    public PaginatedResponseDto findAvailableConferenceRooms(Boolean available, int page) {
//        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
//        Page<ConferenceRoom> roomPage = repo.findByAvailable(available, pageable);
//
//        return buildPaginatedResponse(roomPage, page);
//    }

    
    private PaginatedResponseDto buildPaginatedResponse(Page<ConferenceRoom> roomPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) roomPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) roomPage.getTotalElements());
        response.setTotalNumberOfPages(roomPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(roomPage.getContent());
        return response;
    }

}

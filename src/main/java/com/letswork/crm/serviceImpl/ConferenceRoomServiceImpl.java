package com.letswork.crm.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ConferenceRoomServiceImpl implements ConferenceRoomService {
	
	@Autowired
	ConferenceRoomRepository repo;
	
	@Autowired
	LetsWorkCentreService letsWorkCentreService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
    S3Service s3Service;
	
	private String bucketName = "letsworkcentres";
	
	ModelMapper mapper = new ModelMapper();

	@Override
	public String saveOrUpdate(
	        ConferenceRoom conferenceRoom,
	        MultipartFile image
	) throws IOException {

	    Tenant tenant =
	            tenantService.findTenantByCompanyId(
	                    conferenceRoom.getCompanyId()
	            );

	    if (tenant == null) {
	        throw new RuntimeException(
	                "Invalid companyId - " + conferenceRoom.getCompanyId()
	        );
	    }

	    LetsWorkCentre centre =
	            letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
	                    conferenceRoom.getLetsWorkCentre(),
	                    conferenceRoom.getCompanyId(),
	                    conferenceRoom.getCity(),
	                    conferenceRoom.getState()
	            );

	    if (centre == null) {
	        throw new RuntimeException(
	                "This LetsWorkCentre does not exist"
	        );
	    }

	    ConferenceRoom existing =
	            repo.findByNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
	                    conferenceRoom.getName(),
	                    conferenceRoom.getLetsWorkCentre(),
	                    conferenceRoom.getCompanyId(),
	                    conferenceRoom.getCity(),
	                    conferenceRoom.getState()
	            );

	    ConferenceRoom saved;

	    if (existing != null) {

	    	mapper.map(conferenceRoom, existing);
	        existing.setUpdateDate(new Date());

	        saved = repo.save(existing);

	    } else {

	        conferenceRoom.setCreateDate(new Date());
	        conferenceRoom.setUpdateDate(new Date());

	        saved = repo.save(conferenceRoom);
	    }

	    if (image != null && !image.isEmpty()) {

	        File tempFile =
	                File.createTempFile(
	                        "conf_room_",
	                        image.getOriginalFilename()
	                );

	        image.transferTo(tempFile);

	        String s3Url =
	                s3Service.uploadConferenceRoomImage(
	                        bucketName,
	                        saved.getCompanyId(),
	                        saved.getLetsWorkCentre(),
	                        saved.getName(),
	                        image.getOriginalFilename(),
	                        tempFile
	                );

	        saved.setS3Path(s3Url);
	        repo.save(saved);

	        tempFile.delete();
	    }

	    return existing != null
	            ? "conference room updated"
	            : "conference room created";
	}
	
	private String validate(ConferenceRoomExcelDto dto) {
		if(dto.getName() == null || dto.getName().length() == 0) {
			return "Room name should not be null";
		}
		
		if(dto.getCapacity() == null) {
			return "Capacity should not be null";
		}
		
		if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
			return "LetsWork Centre should not be null";
		}
		
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId should not be null";
		}
		
		if(dto.getHasProjector()==null) {
			return "Has projector should not be null";
		}
		
		if(dto.getHasWhiteBoard()==null) {
			return "Has White Board should not be null";
		}
		
		if(dto.getHasChargingPorts()==null) {
			return "Has Charging ports should not be null";
		}
		
		if(dto.getCity() == null || dto.getCity().length() == 0) {
			return "City Should not be null";	
			}
		
		if(dto.getState() == null || dto.getState().length() == 0) {
			return "State Should not be null";	
			}
		
		if(tenantService.findTenantByCompanyId(dto.getCompanyId())==null) {
			return "CompanyId "+dto.getCompanyId()+" does not exists";
		}
		
		if(letsWorkCentreService.findByName(dto.getLetsWorkCentre(), dto.getCompanyId(), dto.getCity(), dto.getState()) == null){
			return "Letswork Cente "+dto.getLetsWorkCentre()+" does not exist";
		}
		
		
		
		return "ok";
	}

	
	@Override
	public String uploadConferenceRooms(MultipartFile file) throws IOException {
	    List<ConferenceRoomExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, ConferenceRoomExcelDto.class);
	    
	    for(ConferenceRoomExcelDto dto : dtos) {
	    	String val = validate(dto);
    		if(!val.equalsIgnoreCase("ok")) {
    			return val;
    		}
    	}
	    
	    List<String> responses = new ArrayList<>();

	    for (ConferenceRoomExcelDto dto : dtos) {
	        try {
	            ConferenceRoom room = ConferenceRoom.builder()
	                    .name(dto.getName().trim())
	                    .capacity(dto.getCapacity())
	                    .letsWorkCentre(dto.getLetsWorkCentre().trim())
	                    .companyId(dto.getCompanyId().trim())
	                    .city(dto.getCity().trim())
	                    .state(dto.getState().trim())
	                    .build();

	            

	           
//	            String result = saveOrUpdate(room);
	            String result = "";

	            responses.add(result + ": " + room.getName());
	        } catch (Exception e) {
	            responses.add("Error saving " + dto.getName() + ": " + e.getMessage());
	        }
	    }

	    return "ok";
	}
		
	

	@Override
	public List<ConferenceRoom> findByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state) {
		// TODO Auto-generated method stub
		
		LetsWorkCentre loc = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state);

	    if (loc == null) {
	        return new ArrayList<>();
	    }

	    
	    return repo.findByLetsWorkCentreAndCompanyIdAndCityAndState(loc.getName(), companyId, city, state);
	}

	@Override
	public String deleteByName(ConferenceRoom conferenceRoom) {
		// TODO Auto-generated method stub
		
		ConferenceRoom room = repo.findByNameAndLetsWorkCentreAndCompanyIdAndCityAndState(conferenceRoom.getName(), conferenceRoom.getLetsWorkCentre(), conferenceRoom.getCompanyId(), conferenceRoom.getCity(), conferenceRoom.getState());
		
		if(room!=null) {
			repo.delete(room);
			return "record deleted";
		}
		else return "record not found";
		
	}
	
	private static final int PAGE_SIZE = 10; 

    
    @Override
    public PaginatedResponseDto findByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page) {
        // Check if letsWorkCentre exists
    	LetsWorkCentre loc = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state);
        if (loc == null) {
            return new PaginatedResponseDto(); 
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        Page<ConferenceRoom> roomPage = repo.findByLetsWorkCentreAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state, pageable);

        return buildPaginatedResponse(roomPage, page);
    }
    
    @Override
    public PaginatedResponseDto listAll(String companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<ConferenceRoom> roomPage = repo.findAllByCompanyId(companyId, pageable);

        if (roomPage.isEmpty()) {
            return new PaginatedResponseDto();
        }

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
    
    @Override
    public PaginatedResponseDto listConferenceRooms(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            String search,
            String sort,
            int page,
            int size
    ) {

        // Default sort: id desc
        Sort sortSpec = Sort.by("id").descending();

        // Parse sort input: name=asc
        if (sort != null && !sort.isEmpty()) {
            String[] parts = sort.split("=");
            if (parts.length == 2) {
                String field = parts[0];
                String direction = parts[1];

                sortSpec = direction.equalsIgnoreCase("asc")
                        ? Sort.by(field).ascending()
                        : Sort.by(field).descending();
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortSpec);

        Page<ConferenceRoom> roomPage =
                repo.searchConferenceRooms(companyId, letsWorkCentre, city, state, search, pageable);

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom(page * size + 1);
        response.setRecordsTo((int) Math.min((page + 1) * size, roomPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) roomPage.getTotalElements());
        response.setTotalNumberOfPages(roomPage.getTotalPages());
        response.setSelectedPage(page);
        response.setList(roomPage.getContent());

        return response;
    }

}

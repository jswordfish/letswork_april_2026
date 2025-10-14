package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.LocationExcelDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Location;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.LocationRepository;
import com.letswork.crm.service.LocationService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;



@Service
@Transactional
public class LocationServiceImpl implements LocationService {
	
	@Autowired
	LocationRepository repo;
	
	@Autowired
	TenantService tenantService;

	@Override
	public String saveOrUpdate(Location location) {
		// TODO Auto-generated method stub
		
		Tenant tenant = tenantService.findTenantByCompanyId(location.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+location.getCompanyId());
			
		}
		
		Location loc = repo.findByNameAndCompanyId(location.getName(), location.getCompanyId());
		
		if(loc!=null) {
			
			loc.setName(location.getName());
			loc.setAddress(location.getAddress());
			loc.setTotalConferenceRooms(location.getTotalConferenceRooms());
			loc.setState(location.getState());
			loc.setCity(location.getCity());
			loc.setHasCafe(location.isHasCafe());
			loc.setAmenities(location.getAmenities());
			
			repo.save(loc);
			return "record updated";
		}
		
		else {
			repo.save(location);
			return "record saved";
		}
		
		
	}
	
	@Override
	public String uploadLocationsFromExcel(MultipartFile file) {
        try {
            List<LocationExcelDto> locations = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, LocationExcelDto.class);

            List<String> responses = locations.stream().map(dto -> {
                Location location = Location.builder()
                        .name(dto.getName())
                        .totalConferenceRooms(dto.getTotalConferenceRooms())
                        .address(dto.getAddress())
                        .companyId(dto.getCompanyId())
                        .state(dto.getState())
                        .city(dto.getCity())
                        .hasCafe(dto.isHasCafe())
                        .amenities(dto.getAmenities())
                        .build();
                return saveOrUpdate(location);
            }).collect(Collectors.toList());

            return "Processed " + locations.size() + " locations successfully.\n"
                    + String.join("\n", responses);

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to process Excel file: " + e.getMessage();
        }
    }

	@Override
	public Location findByName(String name) {
		Location loc = repo.findByName(name);
		return loc;
	}

	@Override
	public List<Location> findAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public String deleteLocation(Location location) {
		// TODO Auto-generated method stub
		
		Location loc = repo.findByNameAndCompanyId(location.getName(), location.getCompanyId());
		if(loc!=null) {
		repo.delete(loc);
		return "record deleted";
		}
		else return "record not found";
		
	}
	
	private static final int PAGE_SIZE = 10; 

    @Override
    public PaginatedResponseDto getAllLocations(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        Page<Location> locationPage = repo.findAll(pageable);

        return buildPaginatedResponse(locationPage, page);
    }

    
    private PaginatedResponseDto buildPaginatedResponse(Page<Location> locationPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) locationPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) locationPage.getTotalElements());
        response.setTotalNumberOfPages(locationPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(locationPage.getContent());
        return response;
    }

}

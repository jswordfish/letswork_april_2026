package com.LetsWork.CRM.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.Location;
import com.LetsWork.CRM.repo.LocationRepository;
import com.LetsWork.CRM.service.LocationService;



@Service
@Transactional
public class LocationServiceImpl implements LocationService {
	
	@Autowired
	LocationRepository repo;

	@Override
	public String saveOrUpdate(Location location) {
		// TODO Auto-generated method stub
		
		Location loc = repo.findByNameAndCompanyId(location.getName(), location.getCompanyId());
		
		if(loc!=null) {
			
			loc.setName(location.getName());
			loc.setAddress(location.getAddress());
			loc.setTotalConferenceRooms(location.getTotalConferenceRooms());
			loc.setTotalSeats(location.getTotalSeats());
			
			repo.save(loc);
			return "record updated";
		}
		
		else {
			repo.save(location);
			return "record saved";
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

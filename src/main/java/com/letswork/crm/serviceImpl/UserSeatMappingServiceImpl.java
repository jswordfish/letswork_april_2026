package com.letswork.crm.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Cabin;
import com.letswork.crm.entities.Client;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.entities.UserSeatMapping;
import com.letswork.crm.repo.ClientRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.SeatRepository;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.repo.UserSeatMappingRepository;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.TenantService;
import com.letswork.crm.service.UserSeatMappingService;

@Service
@Transactional
public class UserSeatMappingServiceImpl implements UserSeatMappingService {

    @Autowired
    private UserSeatMappingRepository userSeatMappingRepository;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private SeatRepository seatRepo;
    
    @Autowired
    TenantService tenantService;
    
    @Autowired
    LetsWorkCentreRepository letsWorkCentreRepo;
    
    @Autowired
	LetsWorkCentreService letsWorkCentreService;
    
    @Autowired
    ClientRepository clientRepo;

    @Override
    public UserSeatMapping saveOrUpdate(UserSeatMapping mapping) {
    	
		Tenant tenant = tenantService.findTenantByCompanyId(mapping.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+mapping.getCompanyId());
			
		}
    	
    	Client client = clientRepo.findByEmailAndCompanyId(mapping.getEmail(), mapping.getCompanyId());
    	
    	if(client==null) {
    		
    		throw new RuntimeException("User does not exists");
    		
    	}
    	
    	Optional<Seat> seat = seatRepo.findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumberAndCityAndStateAndPublishedTrue(mapping.getSeatType(), mapping.getCompanyId(), mapping.getLetsWorkCentre(), mapping.getSeatNumber(), mapping.getCity(), mapping.getState());
    	
    	if(seat.isEmpty()) {
    		throw new RuntimeException("Seat does not exists or is not published");
    	}
    	
        Optional<UserSeatMapping> existingMappingOpt =
                userSeatMappingRepository.findByEmailAndCompanyIdAndLetsWorkCentreAndCityAndState(
                        mapping.getEmail(), mapping.getCompanyId(), mapping.getLetsWorkCentre(), mapping.getCity(), mapping.getState());

        if (existingMappingOpt.isPresent()) {
            UserSeatMapping existing = existingMappingOpt.get();

            existing.setSeatType(mapping.getSeatType());
            existing.setSeatNumber(mapping.getSeatNumber());
            existing.setNumberOfDays(mapping.getNumberOfDays());
            existing.setCity(mapping.getCity());
            existing.setState(mapping.getState());

            return userSeatMappingRepository.save(existing);
        } else {
            
            return userSeatMappingRepository.save(mapping);
        }
    }

    @Override
    public PaginatedResponseDto listMappings(String companyId, String letsWorkCentre, String city, String state, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("id").descending());
        Page<UserSeatMapping> page = userSeatMappingRepository.findByCompanyIdAndLetsWorkCentreAndCityAndState(companyId, letsWorkCentre, city, state, pageable);

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((pageNo - 1) * pageSize + 1);
        response.setRecordsTo((int) Math.min(pageNo * pageSize, page.getTotalElements()));
        response.setTotalNumberOfRecords((int) page.getTotalElements());
        response.setTotalNumberOfPages(page.getTotalPages());
        response.setSelectedPage(pageNo);
        response.setList(page.getContent());

        return response;
    }

    @Override
    public void deleteMapping(Long id) {
        userSeatMappingRepository.deleteById(id);
    }
    
    @Override
    public PaginatedResponseDto findByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page) {
        // Check if letsWorkCentre exists
    	
		Tenant tenant = tenantService.findTenantByCompanyId(companyId);
        
        if(tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + companyId);
        }
        
        LetsWorkCentre loc = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state);
        
        if(loc == null) {
            throw new RuntimeException("This letsWorkCentre does not exists");
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("email").ascending());
        Page<UserSeatMapping> mappingPage = userSeatMappingRepository.findByLetsWorkCentreAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state, pageable);

        return buildPaginatedResponse(mappingPage, page);
    }
    
    
    private static final int PAGE_SIZE = 10;
    
    private PaginatedResponseDto buildPaginatedResponse(Page<UserSeatMapping> mappingPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) mappingPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) mappingPage.getTotalElements());
        response.setTotalNumberOfPages(mappingPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(mappingPage.getContent());
        return response;
    }
    
    
}

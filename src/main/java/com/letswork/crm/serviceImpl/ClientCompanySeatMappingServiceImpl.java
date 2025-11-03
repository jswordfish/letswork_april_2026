package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ClientCompany;
import com.letswork.crm.entities.ClientCompanySeatMapping;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.entities.UserSeatMapping;
import com.letswork.crm.repo.ClientCompanyRepository;
import com.letswork.crm.repo.ClientCompanySeatMappingRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.SeatRepository;
import com.letswork.crm.repo.UserSeatMappingRepository;
import com.letswork.crm.service.ClientCompanySeatMappingService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class ClientCompanySeatMappingServiceImpl implements ClientCompanySeatMappingService{
	
	@Autowired
	ClientCompanySeatMappingRepository repo;
	
	@Autowired
    TenantService tenantService;
	
	@Autowired
    SeatRepository seatRepo;
	
	@Autowired
    LetsWorkCentreRepository letsWorkCentreRepo;
	
	@Autowired
	ClientCompanyRepository clientCompanyRepo;
	
	@Autowired
	UserSeatMappingRepository userSeatMappingRepository;
	
	
	private static final int PAGE_SIZE = 10;
	
	@Override
	public ClientCompanySeatMapping saveOrUpdate(ClientCompanySeatMapping mapping) {

	    Tenant tenant = tenantService.findTenantByCompanyId(mapping.getCompanyId());
	    if (tenant == null) {
	        throw new RuntimeException("Invalid CompanyId - " + mapping.getCompanyId());
	    }

	    
	    LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
	            mapping.getLetsWorkCentre(), mapping.getCompanyId(), mapping.getCity(), mapping.getState());
	    if (centre == null) {
	        throw new RuntimeException("Invalid LetsWorkCentre details");
	    }

	    
	    Optional<Seat> seat = seatRepo.findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumberAndCityAndStateAndPublishedTrue(
	            mapping.getSeatType(), mapping.getCompanyId(), mapping.getLetsWorkCentre(),
	            mapping.getSeatNumber(), mapping.getCity(), mapping.getState());

	    if (seat.isEmpty()) {
	        throw new RuntimeException("Seat does not exist or is not published");
	    }
	    
	    ClientCompany company = clientCompanyRepo.findByClientCompanyNameAndCompanyIdAndCityAndStateAndLetsWorkCentre(mapping.getClientCompanyName(), mapping.getCompanyId(), mapping.getCity(), mapping.getState(), mapping.getLetsWorkCentre());
	    
	    if(company==null) {
	    	throw new RuntimeException("This company does not exists");
	    }
	    
	    
	    Optional<ClientCompanySeatMapping> seatAssignedOpt =
	            repo.findBySeatNumberAndSeatTypeAndLetsWorkCentreAndCompanyIdAndCityAndState(
	                    mapping.getSeatNumber(),
	                    mapping.getSeatType(),
	                    mapping.getLetsWorkCentre(),
	                    mapping.getCompanyId(),
	                    mapping.getCity(),
	                    mapping.getState()
	            );

	    if (seatAssignedOpt.isPresent()) {
	        ClientCompanySeatMapping seatAssigned = seatAssignedOpt.get();
	        if (!seatAssigned.getClientCompanyName().equalsIgnoreCase(mapping.getClientCompanyName())) {
	            throw new RuntimeException("Seat " + mapping.getSeatNumber() + " is already assigned to another company: " + seatAssigned.getClientCompanyName());
	        }
	    }
	    
	    Optional<UserSeatMapping> seatAssignedOpt1 =
                userSeatMappingRepository.findBySeatNumberAndSeatTypeAndLetsWorkCentreAndCompanyIdAndCityAndState(
                        mapping.getSeatNumber(), mapping.getSeatType(), mapping.getLetsWorkCentre(),
                        mapping.getCompanyId(), mapping.getCity(), mapping.getState());

        if (seatAssignedOpt1.isPresent()) {
            UserSeatMapping seatAssigned = seatAssignedOpt1.get();
            
            throw new RuntimeException("Seat " + mapping.getSeatNumber() + " is already assigned to another user: " + seatAssigned.getEmail());
            
        }
	    
	    
	    Optional<ClientCompanySeatMapping> existingOpt = repo.findByFullBusinessKey(
	            mapping.getClientCompanyName(), mapping.getLetsWorkCentre(),
	            mapping.getCompanyId(), mapping.getCity(), mapping.getState(),
	            mapping.getSeatType(), mapping.getSeatNumber());

	    ModelMapper mapper = new ModelMapper();

	    if (existingOpt.isPresent()) {
	        ClientCompanySeatMapping existing = existingOpt.get();
	        mapping.setId(existing.getId());
	        mapping.setUpdateDate(new Date());
	        mapper.map(mapping, existing);  
	        return repo.save(existing);
	    } else {
	        mapping.setCreateDate(new Date());
	        return repo.save(mapping);
	    }
	}
	
	@Override
    public PaginatedResponseDto listByLetsWorkCentre(String companyId, String letsWorkCentre, String city, String state, int page) {
        Tenant tenant = tenantService.findTenantByCompanyId(companyId);
        if (tenant == null) {
            throw new RuntimeException("Invalid CompanyId - " + companyId);
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending());
        Page<ClientCompanySeatMapping> mappings;

        
        if ((letsWorkCentre == null || letsWorkCentre.isEmpty())
                && (city == null || city.isEmpty())
                && (state == null || state.isEmpty())) {
            mappings = repo.findByCompanyId(companyId, pageable);
        } else {
            
            mappings = repo.findByLetsWorkCentreAndCompanyIdAndCityAndState(
                    letsWorkCentre, companyId, city, state, pageable);
        }

        return buildPaginatedResponse(mappings, page);
    }

    @Override
    public PaginatedResponseDto listForSpecificClient(String clientCompanyName, String letsWorkCentre,
                                                      String companyId, String city, String state, int page) {
        Tenant tenant = tenantService.findTenantByCompanyId(companyId);
        if (tenant == null) {
            throw new RuntimeException("Invalid CompanyId - " + companyId);
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending());
        Page<ClientCompanySeatMapping> pageData = repo.findByClientCompanyNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
                clientCompanyName, letsWorkCentre, companyId, city, state, pageable);

        return buildPaginatedResponse(pageData, page);
    }

    @Override
    public String deleteMapping(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Mapping not found with ID: " + id);
        }
        repo.deleteById(id);
        return "Mapping deleted successfully";
    }

    private PaginatedResponseDto buildPaginatedResponse(Page<ClientCompanySeatMapping> page, int pageNo) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((pageNo) * PAGE_SIZE + 1);
        response.setRecordsTo((int) Math.min((pageNo + 1) * PAGE_SIZE, page.getTotalElements()));
        response.setTotalNumberOfRecords((int) page.getTotalElements());
        response.setTotalNumberOfPages(page.getTotalPages());
        response.setSelectedPage(pageNo + 1);
        response.setList(page.getContent());
        return response;
    }
	

}

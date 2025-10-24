package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.CabinExcelDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Cabin;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.CabinRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.CabinService;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;

@Service
@Transactional
public class CabinServiceImpl implements CabinService {

    @Autowired
    private CabinRepository cabinRepository;
    
    @Autowired
    TenantService tenantService;
    
    @Autowired
    LetsWorkCentreRepository letsWorkCentreRepo;
    
    @Autowired
	LetsWorkCentreService letsWorkCentreService;
    
    ModelMapper mapper = new ModelMapper();

    @Override
    public synchronized Cabin saveOrUpdate(Cabin cabin) {
    	
		Tenant tenant = tenantService.findTenantByCompanyId(cabin.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+cabin.getCompanyId());
			
		}

		
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(cabin.getLetsWorkCentre(), cabin.getCompanyId(), cabin.getCity(), cabin.getState());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}

    	
        if (!StringUtils.hasText(cabin.getCabinName()) || !StringUtils.hasText(cabin.getLetsWorkCentre())) {
            throw new RuntimeException("Cabin name and letsWorkCentre are required.");
        }

        Optional<Cabin> existing = cabinRepository.findByCabinNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
                cabin.getCabinName(), cabin.getLetsWorkCentre(), cabin.getCompanyId(), cabin.getCity(), cabin.getState());

        if (existing.isPresent()) {
            Cabin old = existing.get();
            cabin.setId(old.getId());
            cabin.setCreateDate(old.getCreateDate());
            cabin.setUpdateDate(new Date());
            
            mapper.map(cabin, old); 
            
            return cabinRepository.save(old);
        } else {
            cabin.setCreateDate(new Date());
            return cabinRepository.save(cabin);
        }
    }

    @Override
    public PaginatedResponseDto listAll(String companyId, int page, int size) {
        Page<Cabin> cabins = cabinRepository.findAll(PageRequest.of(page, size));
        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setSelectedPage(page + 1);
        dto.setTotalNumberOfPages(cabins.getTotalPages());
        dto.setTotalNumberOfRecords((int) cabins.getTotalElements());
        dto.setRecordsFrom(page * size + 1);
        dto.setRecordsTo(page * size + cabins.getNumberOfElements());
        dto.setList(cabins.getContent());
        return dto;
    }

    @Override
    public void delete(Long id) {
        cabinRepository.deleteById(id);
    }
    
    private String validate(CabinExcelDto dto) {
		if(dto.getCabinName() == null || dto.getCabinName().length() == 0) {
			return "Cabin name Should not be null";
		}
		
		if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
			return "LetsWork Centre Should not be null";	
			}
		
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId Should not be null";	
			}
		
		if(dto.getCabinNumber() == null || dto.getCabinNumber().length() == 0) {
			return "Cabin number Should not be null";	
			}
		
		if(dto.getTotalSeats() == null) {
			return "Total seats Should not be null";	
			}
		
		if(dto.getDescription() == null || dto.getDescription().length() == 0) {
			return "Description Should not be null";	
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
    public String uploadCabins(MultipartFile file) throws IOException {
        List<CabinExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, CabinExcelDto.class);
        
        for(CabinExcelDto dto : dtos) {
        	String val = validate(dto);
    		if(!val.equalsIgnoreCase("ok")) {
    			return val;
    		}
    	}
        
        List<String> responses = new ArrayList<>();

        for (CabinExcelDto dto : dtos) {
            try {
                Cabin cabin = Cabin.builder()
                        .cabinName(dto.getCabinName().trim())
                        .letsWorkCentre(dto.getLetsWorkCentre().trim())
                        .cabinNumber(dto.getCabinNumber().trim())
                        .totalSeats(dto.getTotalSeats())
                        .description(dto.getDescription().trim())
                        .companyId(dto.getCompanyId().trim())
                        .city(dto.getCity().trim())
                        .state(dto.getState().trim())
                        .build();

                saveOrUpdate(cabin);
                responses.add("Saved/Updated: " + cabin.getCabinName());
            } catch (Exception e) {
                responses.add("Error saving " + dto.getCabinName() + ": " + e.getMessage());
            }
        }
        return "ok";
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

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("cabinName").ascending());
        Page<Cabin> cabinPage = cabinRepository.findByLetsWorkCentreAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state, pageable);

        return buildPaginatedResponse(cabinPage, page);
    }
    
    
    private static final int PAGE_SIZE = 10;
    
    private PaginatedResponseDto buildPaginatedResponse(Page<Cabin> cabinPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) cabinPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) cabinPage.getTotalElements());
        response.setTotalNumberOfPages(cabinPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(cabinPage.getContent());
        return response;
    }
    
}

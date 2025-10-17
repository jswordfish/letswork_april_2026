package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.LetsWorkCentreExcelDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;



@Service
@Transactional
public class LetsWorkCentreServiceImpl implements LetsWorkCentreService {
	
	@Autowired
	LetsWorkCentreRepository repo;
	
	@Autowired
	TenantService tenantService;
	
	ModelMapper mapper = new ModelMapper();

	@Override
	public String saveOrUpdate(LetsWorkCentre letsWorkCentre) {
		// TODO Auto-generated method stub
		
		Tenant tenant = tenantService.findTenantByCompanyId(letsWorkCentre.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+letsWorkCentre.getCompanyId());
			
		}
		
		
		LetsWorkCentre loc = repo.findByNameAndCompanyIdAndCityAndState(letsWorkCentre.getName(), letsWorkCentre.getCompanyId(), letsWorkCentre.getCity(), letsWorkCentre.getState());
		
		if(loc!=null) {
			

			letsWorkCentre.setId(loc.getId());
			letsWorkCentre.setUpdateDate(new Date());
			mapper.map(letsWorkCentre, loc);
			
			repo.save(loc);
			return "record updated";
		}
		
		else {
			letsWorkCentre.setCreateDate(new Date());
			repo.save(letsWorkCentre);
			return "record saved";
		}
		
		
	}
	
	
	private String validate(LetsWorkCentreExcelDto dto) {
		if(dto.getName() == null || dto.getName().length() == 0) {
			return "Name Should not be null";
		}
		
		if(dto.getState() == null || dto.getState().length() == 0) {
			return "State Should not be null";		
			}
		
		if(dto.getCity() == null || dto.getCity().length() == 0) {
			return "City Should not be null";	
			}
		
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId Should not be null";	
			}
		
		if(dto.getTotalConferenceRooms() == null) {
			return "Total Conference Rooms Should not be null";	
			}
		
		if(dto.getAddress() == null || dto.getAddress().length() == 0) {
			return "Address Should not be null";	
			}
		
		if(dto.getAmenities() == null || dto.getAmenities().length() == 0) {
			return "Amenities Should not be null";	
			}
		
		if(dto.getHasCafe()==null) {
			return "Cafe boolean Should not be null";	
			}
		
		if(tenantService.findTenantByCompanyId(dto.getCompanyId())==null) {
			return "CompanyId "+dto.getCompanyId()+" does not exists";
		}
		
		
		return "ok";
	}
	
	
	@Override
	public String uploadLetsWorkCentresFromExcel(MultipartFile file) {
        try {
            List<LetsWorkCentreExcelDto> letsWorkCentres = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, LetsWorkCentreExcelDto.class);
            
            for(LetsWorkCentreExcelDto dto : letsWorkCentres) {
            	String val = validate(dto);
        		if(!val.equalsIgnoreCase("ok")) {
        			return val;
        		}
        	}
            
            List<String> responses = letsWorkCentres.stream().map(dto -> {
            	LetsWorkCentre letsWorkCentre = LetsWorkCentre.builder()
                        .name(dto.getName().trim())
                        .totalConferenceRooms(dto.getTotalConferenceRooms())
                        .address(dto.getAddress().trim())
                        .companyId(dto.getCompanyId().trim())
                        .state(dto.getState().trim())
                        .city(dto.getCity().trim())
                        .hasCafe(dto.getHasCafe())
                        .amenities(dto.getAmenities().trim())
                        .build();
                return saveOrUpdate(letsWorkCentre);
            }).collect(Collectors.toList());

//            return "Processed " + letsWorkCentres.size() + " LetsWorkCentre successfully.\n"
//                    + String.join("\n", responses);
            return "ok";

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to process Excel file: " + e.getMessage();
        }
    }

	@Override
	public LetsWorkCentre findByName(String name, String companyId, String city, String state) {
		LetsWorkCentre loc = repo.findByNameAndCompanyIdAndCityAndState(name, companyId, city, state);
		return loc;
	}

	@Override
	public List<LetsWorkCentre> findAll(String companyId) {
		// TODO Auto-generated method stub
		return repo.findAllByCompanyId(companyId);
	}

	@Override
	public String deleteLetsWorkCentre(LetsWorkCentre letsWorkCentre) {
		// TODO Auto-generated method stub
		
		LetsWorkCentre loc = repo.findByNameAndCompanyIdAndCityAndState(letsWorkCentre.getName(), letsWorkCentre.getCompanyId(), letsWorkCentre.getCity(), letsWorkCentre.getState());
		if(loc!=null) {
		repo.delete(loc);
		return "record deleted";
		}
		else return "record not found";
		
	}
	
	private static final int PAGE_SIZE = 10; 

	@Override
    public PaginatedResponseDto getAllLetsWorkCentres(int page, String companyId) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        Page<LetsWorkCentre> letsWorkCentrePage = repo.findAllByCompanyId(companyId, pageable);

        return buildPaginatedResponse(letsWorkCentrePage, page);
    }

    
    private PaginatedResponseDto buildPaginatedResponse(Page<LetsWorkCentre> letsWorkCentrePage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) letsWorkCentrePage.getTotalElements()));
        response.setTotalNumberOfRecords((int) letsWorkCentrePage.getTotalElements());
        response.setTotalNumberOfPages(letsWorkCentrePage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(letsWorkCentrePage.getContent());
        return response;
    }

}

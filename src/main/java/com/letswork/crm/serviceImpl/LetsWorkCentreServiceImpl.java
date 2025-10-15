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

	@Override
	public String saveOrUpdate(LetsWorkCentre LetsWorkCentre) {
		// TODO Auto-generated method stub
		
		Tenant tenant = tenantService.findTenantByCompanyId(LetsWorkCentre.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+LetsWorkCentre.getCompanyId());
			
		}
		
		LetsWorkCentre loc = repo.findByNameAndCompanyId(LetsWorkCentre.getName(), LetsWorkCentre.getCompanyId());
		
		if(loc!=null) {
			
			loc.setName(LetsWorkCentre.getName());
			loc.setAddress(LetsWorkCentre.getAddress());
			loc.setTotalConferenceRooms(LetsWorkCentre.getTotalConferenceRooms());
			loc.setState(LetsWorkCentre.getState());
			loc.setCity(LetsWorkCentre.getCity());
			loc.setHasCafe(LetsWorkCentre.isHasCafe());
			loc.setAmenities(LetsWorkCentre.getAmenities());
			
			repo.save(loc);
			return "record updated";
		}
		
		else {
			repo.save(LetsWorkCentre);
			return "record saved";
		}
		
		
	}
	
	@Override
	public String uploadLetsWorkCentresFromExcel(MultipartFile file) {
        try {
            List<LetsWorkCentreExcelDto> letsWorkCentres = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, LetsWorkCentreExcelDto.class);

            List<String> responses = letsWorkCentres.stream().map(dto -> {
            	LetsWorkCentre letsWorkCentre = LetsWorkCentre.builder()
                        .name(dto.getName())
                        .totalConferenceRooms(dto.getTotalConferenceRooms())
                        .address(dto.getAddress())
                        .companyId(dto.getCompanyId())
                        .state(dto.getState())
                        .city(dto.getCity())
                        .hasCafe(dto.isHasCafe())
                        .amenities(dto.getAmenities())
                        .build();
                return saveOrUpdate(letsWorkCentre);
            }).collect(Collectors.toList());

            return "Processed " + letsWorkCentres.size() + " LetsWorkCentre successfully.\n"
                    + String.join("\n", responses);

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to process Excel file: " + e.getMessage();
        }
    }

	@Override
	public LetsWorkCentre findByName(String name) {
		LetsWorkCentre loc = repo.findByName(name);
		return loc;
	}

	@Override
	public List<LetsWorkCentre> findAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public String deleteLetsWorkCentre(LetsWorkCentre letsWorkCentre) {
		// TODO Auto-generated method stub
		
		LetsWorkCentre loc = repo.findByNameAndCompanyId(letsWorkCentre.getName(), letsWorkCentre.getCompanyId());
		if(loc!=null) {
		repo.delete(loc);
		return "record deleted";
		}
		else return "record not found";
		
	}
	
	private static final int PAGE_SIZE = 10; 

    @Override
    public PaginatedResponseDto getAllLetsWorkCentres(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        Page<LetsWorkCentre> letsWorkCentrePage = repo.findAll(pageable);

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

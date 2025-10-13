package com.LetsWork.CRM.serviceImpl;

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

import com.LetsWork.CRM.dtos.ClientCompanyExcelDto;
import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.ClientCompany;
import com.LetsWork.CRM.repo.ClientCompanyRepository;
import com.LetsWork.CRM.repo.ClientRepository;
import com.LetsWork.CRM.repo.LocationRepository;
import com.LetsWork.CRM.service.ClientCompanyService;
import com.LetsWork.CRM.service.LocationService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;



@Service
@Transactional
public class ClientCompanyServiceImpl implements ClientCompanyService {
	
	@Autowired
	ClientCompanyRepository repo;
	
	@Autowired
	ClientRepository clientRepo;
	
	@Autowired
	LocationService locationService;
	
	@Autowired
	LocationRepository locationRepo;

	@Override
	public String saveOrUpdate(ClientCompany clientCompany) {
		// TODO Auto-generated method stub
		
		ClientCompany com = repo.findByClientCompanyNameAndLocationAndCompanyId(clientCompany.getClientCompanyName(), clientCompany.getLocation(), clientCompany.getCompanyId());
		
		if(com!=null) {
			
			com.setClientCompanyName(clientCompany.getClientCompanyName());
			
			com.setIndustry(clientCompany.getIndustry());
			com.setLocation(clientCompany.getLocation());
		
			repo.save(com);
			return "record updated";
			
		}
		
		else {
			repo.save(clientCompany);
			return "record saved";
		}
		
	}
	
	@Override
	public List<String> uploadClientCompanies(MultipartFile file, String companyId) throws IOException {
	    List<ClientCompanyExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, ClientCompanyExcelDto.class);
	    List<String> responses = new ArrayList<>();

	    for (ClientCompanyExcelDto dto : dtos) {
	        try {
	            ClientCompany company = ClientCompany.builder()
	                    .clientCompanyName(dto.getClientCompanyName())
	                    .industry(dto.getIndustry())
	                    .location(dto.getLocation())
	                    .build();
	            company.setCompanyId(companyId);

	            String result = saveOrUpdate(company);

	            responses.add(result + company.getClientCompanyName());
	        } catch (Exception e) {
	            responses.add("Error saving " + dto.getClientCompanyName() + ": " + e.getMessage());
	        }
	    }
	    return responses;
	}

	@Override
	public List<ClientCompany> findByIndustry(String industry) {
		// TODO Auto-generated method stub
		return repo.findByIndustry(industry);
	}

	@Override
	public List<ClientCompany> listAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public String deleteCompany(ClientCompany clientCompany) {
		// TODO Auto-generated method stub
		ClientCompany com = repo.findByClientCompanyNameAndLocationAndCompanyId(clientCompany.getClientCompanyName(), clientCompany.getLocation(), clientCompany.getCompanyId());
		
		if(com!=null) {
			repo.delete(com);
			return "record deleted";
		}
		else return "record not found";
		
	}

	

	@Override
	public List<ClientCompany> getClientCompaniesByLocation(String location, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByLocationAndCompanyId(location, companyId);
	}
	
	private static final int PAGE_SIZE = 10; 

    
    @Override
    public PaginatedResponseDto listAll(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("companyName").ascending());
        Page<ClientCompany> companyPage = repo.findAll(pageable);

        return buildPaginatedResponse(companyPage, page);
    }

    
    @Override
    public PaginatedResponseDto getClientCompaniesByLocation(String location, String companyId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("companyName").ascending());
        Page<ClientCompany> companyPage = repo.findByLocationAndCompanyId(location, companyId, pageable);

        return buildPaginatedResponse(companyPage, page);
    }

    
    private PaginatedResponseDto buildPaginatedResponse(Page<ClientCompany> companyPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) companyPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) companyPage.getTotalElements());
        response.setTotalNumberOfPages(companyPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(companyPage.getContent());
        return response;
    }


	

}

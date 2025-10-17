package com.letswork.crm.serviceImpl;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.ClientCompanyExcelDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ClientCompany;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.ClientCompanyRepository;
import com.letswork.crm.repo.ClientRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.ClientCompanyService;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.TenantService;
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
	LetsWorkCentreService LetsWorkCentreService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreService letsWorkCentreService;
	
	ModelMapper mapper = new ModelMapper();

	@Override
	public String saveOrUpdate(ClientCompany clientCompany) {
		// TODO Auto-generated method stub
		
		Tenant tenant = tenantService.findTenantByCompanyId(clientCompany.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+clientCompany.getCompanyId());
			
		}
		
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(clientCompany.getLetsWorkCentre(), clientCompany.getCompanyId(), clientCompany.getCity(), clientCompany.getState());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}
		
		
		ClientCompany com = repo.findByClientCompanyNameAndCompanyId(clientCompany.getClientCompanyName(), clientCompany.getCompanyId());
		
		if(com!=null) {
			
//			com.setClientCompanyName(clientCompany.getClientCompanyName());
//			
//			com.setIndustry(clientCompany.getIndustry());
//			com.setLetsWorkCentre(clientCompany.getLetsWorkCentre());
			
			clientCompany.setId(com.getId());
			clientCompany.setUpdateDate(new Date());
			mapper.map(clientCompany, com);
		
			repo.save(com);
			return "record updated";
			
		}
		
		else {
			clientCompany.setCreateDate(new Date());
			repo.save(clientCompany);
			return "record saved";
		}
		
	}
	
	private String validate(ClientCompanyExcelDto dto) {
		if(dto.getClientCompanyName() == null || dto.getClientCompanyName().length() == 0) {
			return "Client company Should not be null";
		}
		
		if(dto.getIndustry() == null || dto.getIndustry().length() == 0) {
			return "Client company Should not be null";		
			}
		
		if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
			return "LetsWork Centre Should not be null";	
			}
		
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId Should not be null";	
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
	public String uploadClientCompanies(MultipartFile file) throws IOException {
	    List<ClientCompanyExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, ClientCompanyExcelDto.class);
	    
	    for(ClientCompanyExcelDto dto : dtos) {
	    	String val = validate(dto);
    		if(!val.equalsIgnoreCase("ok")) {
    			return val;
    		}
    	}
	    
	    List<String> responses = new ArrayList<>();

	    for (ClientCompanyExcelDto dto : dtos) {
	        try {
	            ClientCompany company = ClientCompany.builder()
	                    .clientCompanyName(dto.getClientCompanyName().trim())
	                    .industry(dto.getIndustry().trim())
	                    .letsWorkCentre(dto.getLetsWorkCentre().trim())
	                    .companyId(dto.getCompanyId().trim())
	                    .city(dto.getCity().trim())
	                    .state(dto.getState().trim())
	                    .build();
	            

	            String result = saveOrUpdate(company);

	            responses.add(result + company.getClientCompanyName());
	        } catch (Exception e) {
	            responses.add("Error saving " + dto.getClientCompanyName() + ": " + e.getMessage());
	        }
	    }
	    return "ok";
	}

	

	@Override
	public List<ClientCompany> listAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public String deleteCompany(ClientCompany clientCompany) {
		// TODO Auto-generated method stub
		ClientCompany com = repo.findByClientCompanyNameAndCompanyId(clientCompany.getClientCompanyName(),  clientCompany.getCompanyId());
		
		if(com!=null) {
			repo.delete(com);
			return "record deleted";
		}
		else return "record not found";
		
	}

	

	@Override
	public List<ClientCompany> getClientCompaniesByLetsWorkCentre(String letsWorkCentre, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByLetsWorkCentreAndCompanyId(letsWorkCentre, companyId);
	}
	
	private static final int PAGE_SIZE = 10; 

    
    @Override
    public PaginatedResponseDto listAll(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("companyName").ascending());
        Page<ClientCompany> companyPage = repo.findAll(pageable);

        return buildPaginatedResponse(companyPage, page);
    }

    
    @Override
    public PaginatedResponseDto getClientCompaniesByLetsWorkCentre(String letsWorkCentre, String companyId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("companyName").ascending());
        Page<ClientCompany> companyPage = repo.findByLetsWorkCentreAndCompanyId(letsWorkCentre, companyId, pageable);

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

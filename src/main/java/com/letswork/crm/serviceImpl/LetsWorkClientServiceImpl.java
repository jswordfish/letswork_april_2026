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
import com.letswork.crm.dtos.UserWithCompaniesDto;
import com.letswork.crm.entities.Category;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.SubCategory;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.CategoryType;
import com.letswork.crm.repo.CategoryRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.LetsworkUserRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.SubCategoryRepository;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.LetsWorkClientService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;

import lombok.RequiredArgsConstructor;



@Service
@Transactional
@RequiredArgsConstructor
public class LetsWorkClientServiceImpl implements LetsWorkClientService {
	
	private final CategoryRepository categoryRepo;
    private final SubCategoryRepository subCategoryRepo;
    private final NewUserRegisterRepository userRepo;
	
	@Autowired
	LetsWorkClientRepository repo;
	
	@Autowired
	LetsworkUserRepository clientRepo;
	
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
	public String saveOrUpdate(LetsWorkClient clientCompany) {
		// TODO Auto-generated method stub
		
		Tenant tenant = tenantService.findTenantByCompanyId(clientCompany.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+clientCompany.getCompanyId());
			
		}
		
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(clientCompany.getLetsWorkCentre(), clientCompany.getCompanyId(), clientCompany.getCity(), clientCompany.getState());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}
		
		Category category =
                categoryRepo.findByNameAndCompanyIdAndCategoryType(
                		clientCompany.getCategory(),
                		clientCompany.getCompanyId(),
                        CategoryType.BUSINESS
                );

        if (category == null) {
            throw new RuntimeException("Invalid category");
        }

        SubCategory subCategory =
                subCategoryRepo.findByNameAndCompanyIdAndCategoryType(
                		clientCompany.getSubCategory(),
                		clientCompany.getCompanyId(),
                        CategoryType.BUSINESS
                );

        if (subCategory == null) {
            throw new RuntimeException("Invalid sub-category");
        }
		
		
        if (clientCompany.getId() != null) {

            LetsWorkClient existing =
                    repo.findByIdAndCompanyId(
                            clientCompany.getId(),
                            clientCompany.getCompanyId()
                    ).orElseThrow(() ->
                            new RuntimeException("Client company not found")
                    );

            clientCompany.setCreateDate(existing.getCreateDate());
            clientCompany.setUpdateDate(new Date());

            mapper.map(clientCompany, existing);

            repo.save(existing);
            return "record updated";
        }

        
        if (repo.existsByClientCompanyNameAndCompanyId(
                clientCompany.getClientCompanyName(),
                clientCompany.getCompanyId()
        )) {
            throw new RuntimeException(
                    "Client company already exists"
            );
        }

        clientCompany.setCreateDate(new Date());
        clientCompany.setUpdateDate(new Date());

        repo.save(clientCompany);
        return "record saved";
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
	            LetsWorkClient company = LetsWorkClient.builder()
	                    .clientCompanyName(dto.getClientCompanyName().trim())
//	                    .industry(dto.getIndustry().trim())
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
	public List<LetsWorkClient> listAll() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public String deleteCompany(LetsWorkClient clientCompany) {
		// TODO Auto-generated method stub
		LetsWorkClient com = repo.findByClientCompanyNameAndCompanyIdAndCityAndStateAndLetsWorkCentre(clientCompany.getClientCompanyName(),  clientCompany.getCompanyId(), clientCompany.getCity(), clientCompany.getState(), clientCompany.getLetsWorkCentre());
		
		if(com!=null) {
			repo.delete(com);
			return "record deleted";
		}
		else return "record not found";
		
	}

	

	@Override
	public List<LetsWorkClient> getClientCompaniesByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state) {
		// TODO Auto-generated method stub
		return repo.findByLetsWorkCentreAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state);
	}
	
	private static final int PAGE_SIZE = 10; 

    
    @Override
    public PaginatedResponseDto listAll(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("companyName").ascending());
        Page<LetsWorkClient> companyPage = repo.findAll(pageable);

        return buildPaginatedResponse(companyPage, page);
    }

    
    @Override
    public PaginatedResponseDto getClientCompaniesByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("companyName").ascending());
        Page<LetsWorkClient> companyPage = repo.findByLetsWorkCentreAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state, pageable);

        return buildPaginatedResponse(companyPage, page);
    }
    
    @Override
    public PaginatedResponseDto getClientCompanies(String companyId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("clientCompanyName").ascending());
        Page<LetsWorkClient> pageResult = repo.findByCompanyId(companyId, pageable);

        return buildPaginatedResponse(pageResult, page);
    }

    
    private PaginatedResponseDto buildPaginatedResponse(Page<LetsWorkClient> companyPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) companyPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) companyPage.getTotalElements());
        response.setTotalNumberOfPages(companyPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(companyPage.getContent());
        return response;
    }


    @Override
    public PaginatedResponseDto listClientCompanies(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            String search,
            String sort,
            int page,
            int size
    ) {

        // Default sort → id desc
        Sort sortSpec = Sort.by("id").descending();

        // Parse sort input like: clientCompanyName=asc
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

        Page<LetsWorkClient> pageResult =
                repo.searchClientCompanies(
                        companyId,
                        letsWorkCentre,
                        city,
                        state,
                        search,
                        pageable
                );

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom(page * size + 1);
        response.setRecordsTo((int) Math.min((page + 1) * size, pageResult.getTotalElements()));
        response.setTotalNumberOfRecords((int) pageResult.getTotalElements());
        response.setTotalNumberOfPages(pageResult.getTotalPages());
        response.setSelectedPage(page);
        response.setList(pageResult.getContent());

        return response;
    }
    
    @Override
    public UserWithCompaniesDto getUserWithCompanies(
            Long userId,
            String companyId
    ) {

        NewUserRegister user =
                userRepo.findByIdAndCompanyId(userId, companyId)
                        .orElseThrow(() ->
                                new RuntimeException("User not found"));

        List<LetsWorkClient> companies =
                repo.findByUserIdAndCompanyId(
                        userId,
                        companyId
                );

        return new UserWithCompaniesDto(user, companies);
    }

}

package com.letswork.crm.serviceImpl;


import java.io.IOException;
import java.util.Collections;
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

import com.letswork.crm.dtos.LetsworkUsertExcelDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.LetsworkUser;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.LetsworkUserRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.LetsworkUserService;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;





@Service
@Transactional
public class LestworkUserServiceImpl implements LetsworkUserService {
	
	@Autowired
	LetsworkUserRepository repo;
	
	@Autowired
	LetsWorkClient clientCompanyRepo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreService letsWorkCentreService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	ModelMapper mapper = new ModelMapper();
	
	
	private static final int PAGE_SIZE = 10;

	@Override
	public String saveOrUpdate(LetsworkUser client) {
		// TODO Auto-generated method stub
		
		Tenant tenant = tenantService.findTenantByCompanyId(client.getCompanyId());
		
		if(tenant==null) {
			throw new RuntimeException("CompanyId invalid - "+client.getCompanyId());
		}
		
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(client.getLetsWorkCentre(), client.getCompanyId(), client.getCity(), client.getState());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}
		
		
		
		LetsworkUser client1 = repo.findByEmailAndCompanyId(client.getEmail(), client.getCompanyId());
		if(client1!=null) {
			
			
			client.setId(client1.getId());
			client.setUpdateDate(new Date());
			mapper.map(client, client1);
			repo.save(client1);
			return "record updated";
			
		}
		
		else {
			client.setCreateDate(new Date());
			repo.save(client);
			return "record saved";
		}
		
	}
	

	@Override
	public String deleteClient(LetsworkUser client) {
		// TODO Auto-generated method stub
		
		LetsworkUser client1 = repo.findByNameAndEmailAndCompanyId( client.getEmail(), client.getCompanyId());
		
		if(client1!=null) {
			repo.delete(client1);
			return "record deleted";
		}
		else return "record not found";
		
	}


	@Override
	public List<LetsworkUser> getClientsByCompany(String companyName, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByClientCompanyNameAndCompanyId(companyName, companyId);
	}


	@Override
	public List<LetsworkUser> getIndividualClients(String companyId) {
		// TODO Auto-generated method stub
		return repo.findIndividualClients(companyId);
	}


	@Override
	public List<LetsworkUser> getIndividualClientsByLetsWorkCentre(String letsWorkCentre, String companyId) {
		// TODO Auto-generated method stub
		return repo.findIndividualClientsByLetsWorkCentre(letsWorkCentre, companyId);
	}


	@Override
	public PaginatedResponseDto getClientsByCompany(String companyName, String companyId, int page) {
		// TODO Auto-generated method stub
		int size = 10; 

	    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

	    Page<LetsworkUser> clientPage = repo.findByClientCompanyNameAndCompanyId(companyName, companyId, pageable);

	    PaginatedResponseDto response = new PaginatedResponseDto();

	    response.setRecordsFrom((page * size) + 1);
	    response.setRecordsTo(Math.min((page + 1) * size, (int) clientPage.getTotalElements()));
	    response.setTotalNumberOfRecords((int) clientPage.getTotalElements());
	    response.setTotalNumberOfPages(clientPage.getTotalPages());
	    response.setSelectedPage(page + 1);
	    response.setList(clientPage.getContent());

	    return response;
	}

	
	
	@Override
    public PaginatedResponseDto getIndividualClients(String companyId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending());
        Page<LetsworkUser> clientPage = repo.findIndividualClients(companyId, pageable);
        return buildPaginatedResponse(clientPage, page);
    }	

    // ✅ Get individual clients by letsWorkCentre
    @Override
    public PaginatedResponseDto getIndividualClientsByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending());
        Page<LetsworkUser> clientPage = repo.findIndividualClientsByLetsWorkCentre(letsWorkCentre, companyId, city, state, pageable);
        return buildPaginatedResponse(clientPage, page);
    }
	
	private PaginatedResponseDto buildPaginatedResponse(Page<LetsworkUser> clientPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) clientPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) clientPage.getTotalElements());
        response.setTotalNumberOfPages(clientPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(clientPage.getContent());
        return response;
    }
	private String validate(LetsworkUsertExcelDto dto) {
		if(dto.getBusinessCategory() == null || dto.getBusinessCategory().length() == 0) {
			return "Business Category not available for "+dto.getEmail();
		}
		
		if(dto.getCompanyIfApplicable() == null || dto.getCompanyIfApplicable().length() == 0) {
			return "Company Name for User not available for "+dto.getEmail();
		}
		
		if(dto.getEmail() == null || dto.getEmail().length() == 0) {
			return "Email for User not available for "+dto.getFirstName();
		}
		
		if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
			return "Letswork Center for User not available for "+dto.getEmail();
		}
		
		
		if(dto.getPhone() == null || dto.getPhone().length() == 0) {
			return "Phone for User not available for "+dto.getEmail();
		}
		
		if(dto.getFirstName() == null || dto.getFirstName().length() == 0) {
			return "First Name for User not available for "+dto.getEmail();
		}
		
		if(dto.getLastName() == null || dto.getLastName().length() == 0) {
			return "Last Name for User not available for "+dto.getEmail();
		}
		
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId for User should not be null";
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
	public String uploadClientsFromExcel(MultipartFile file) {
        try {
            List<LetsworkUsertExcelDto> clientsFromExcel = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, LetsworkUsertExcelDto.class);
            	for(LetsworkUsertExcelDto dto : clientsFromExcel) {
            		String val = validate(dto);
            		if(!val.equalsIgnoreCase("ok")) {
            			return val;
            		}
            	}
            
            List<String> responses = clientsFromExcel.stream().map(dto -> {
                LetsworkUser client = LetsworkUser.builder()
                        .firstName(dto.getFirstName().trim())
                        .lastName(dto.getLastName().trim())
                        .email(dto.getEmail().trim())
                        .phone(dto.getPhone().trim())
                        .companyIfApplicable(dto.getCompanyIfApplicable().trim())
                        .letsWorkCentre(dto.getLetsWorkCentre().trim())
                        .companyId(dto.getCompanyId().trim())
                        .businessCategory(dto.getBusinessCategory().trim())
                        .city(dto.getCity().trim())
                        .state(dto.getState().trim())
                        .build();
                	
                return saveOrUpdate(client);
            }).collect(Collectors.toList());

//            return "Processed " + clientsFromExcel.size() + " clients successfully.\n" +
//                    String.join("\n", responses);
            return "ok";

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to process Excel file: " + e.getMessage();
        }
    }

	@Override
	public LetsworkUser getByEmail(String email, String companyId) {
		
		LetsworkUser client = repo.findByEmailAndCompanyId(email, companyId);
		
		return client;
		
	}

	@Override
	public PaginatedResponseDto getClients(String companyId, String email, String letsWorkCentre, String city,
	                                       String state, String search, String sort,
	                                       int pageNo, int pageSize) {
		
		if(email!=null){
			LetsworkUser client = repo.findByEmailAndCompanyId(email, companyId);

		    PaginatedResponseDto response = new PaginatedResponseDto();

		    if (client != null) {
		        response.setRecordsFrom(1);
		        response.setRecordsTo(1);
		        response.setTotalNumberOfRecords(1);
		        response.setTotalNumberOfPages(1);
		        response.setSelectedPage(0);
		        response.setList(Collections.singletonList(client));
		    } else {
		        response.setRecordsFrom(0);
		        response.setRecordsTo(0);
		        response.setTotalNumberOfRecords(0);
		        response.setTotalNumberOfPages(0);
		        response.setSelectedPage(0);
		        response.setList(Collections.emptyList());
		    }

		    return response;
		}

		Sort sorting = Sort.by("id").descending(); // default sorting

	    if (sort != null && !sort.isBlank()) {
	        try {
	            String[] parts = sort.split("=");
	            String field = parts[0];
	            String dir = parts[1];

	            if ("desc".equalsIgnoreCase(dir)) {
	                sorting = Sort.by(field).descending();
	            } else {
	                sorting = Sort.by(field).ascending();
	            }
	        } catch (Exception e) {
	            // fallback to default
	            sorting = Sort.by("id").descending();
	        }
	    }

	    Pageable pageable = PageRequest.of(pageNo, pageSize, sorting);

	    Page<LetsworkUser> page = repo.searchClients(companyId, letsWorkCentre, city, state, search, pageable);

	    return buildPaginatedResponse(page, pageNo);
	}
	

}

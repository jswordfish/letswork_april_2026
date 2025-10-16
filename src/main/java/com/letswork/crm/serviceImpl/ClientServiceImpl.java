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

import com.letswork.crm.dtos.ClientExcelDto;
import com.letswork.crm.dtos.ConferenceRoomExcelDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Client;
import com.letswork.crm.entities.ClientCompany;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.ClientCompanyRepository;
import com.letswork.crm.repo.ClientRepository;
import com.letswork.crm.service.ClientService;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;





@Service
@Transactional
public class ClientServiceImpl implements ClientService {
	
	@Autowired
	ClientRepository repo;
	
	@Autowired
	ClientCompanyRepository clientCompanyRepo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreService letsWorkCentreService;
	
	ModelMapper mapper = new ModelMapper();
	
	
	private static final int PAGE_SIZE = 10;

	@Override
	public String saveOrUpdate(Client client) {
		// TODO Auto-generated method stub
		
		Tenant tenant = tenantService.findTenantByCompanyId(client.getCompanyId());
		
		if(tenant==null) {
			throw new RuntimeException("CompanyId invalid - "+client.getCompanyId());
		}
		
		ClientCompany company = clientCompanyRepo.findByClientCompanyNameAndCompanyId(client.getClientCompanyName(),  client.getCompanyId());
		
		if(company == null) {
//			return "No company with the name "+client.getClientCompanyName()+" exists";
			ClientCompany comp = ClientCompany.builder()
					.letsWorkCentre(client.getLetsWorkCentre())
					.clientCompanyName(client.getClientCompanyName())
					.industry(client.getBusinessCategory())
					.companyId(client.getCompanyId())//updated company id
					.build();
			
			clientCompanyRepo.save(comp);
		}
		
		Client client1 = repo.findByEmailAndCompanyId(client.getEmail(), client.getCompanyId());
		if(client1!=null) {
			
			if(!client.getClientCompanyName().equals(client1.getClientCompanyName())) {
				//this means client has changed his company
				deleteOldCompanyIfNotHavingOtherClients(client1.getClientCompanyName(), client1.getCompanyId());
			}
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
	
	private void deleteOldCompanyIfNotHavingOtherClients(String clientCompanyName, String companyId) {
		if(repo.getCountOfClientsInClientCompany(clientCompanyName, companyId) == 1) {
			//delete the old client company as it is no longer used
			clientCompanyRepo.delete(clientCompanyRepo.findByClientCompanyNameAndCompanyId(clientCompanyName, companyId));
		}
	}


	

	@Override
	public String deleteClient(Client client) {
		// TODO Auto-generated method stub
		
		Client client1 = repo.findByNameAndEmailAndCompanyId( client.getEmail(), client.getCompanyId());
		
		if(client1!=null) {
			repo.delete(client1);
			return "record deleted";
		}
		else return "record not found";
		
	}


	@Override
	public List<Client> getClientsByCompany(String companyName, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByClientCompanyNameAndCompanyId(companyName, companyId);
	}


	@Override
	public List<Client> getIndividualClients(String companyId) {
		// TODO Auto-generated method stub
		return repo.findIndividualClients(companyId);
	}


	@Override
	public List<Client> getIndividualClientsByLetsWorkCentre(String letsWorkCentre, String companyId) {
		// TODO Auto-generated method stub
		return repo.findIndividualClientsByLetsWorkCentre(letsWorkCentre, companyId);
	}


	@Override
	public PaginatedResponseDto getClientsByCompany(String companyName, String companyId, int page) {
		// TODO Auto-generated method stub
		int size = 10; 

	    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

	    Page<Client> clientPage = repo.findByClientCompanyNameAndCompanyId(companyName, companyId, pageable);

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
        Page<Client> clientPage = repo.findIndividualClients(companyId, pageable);
        return buildPaginatedResponse(clientPage, page);
    }

    // ✅ Get individual clients by letsWorkCentre
    @Override
    public PaginatedResponseDto getIndividualClientsByLetsWorkCentre(String letsWorkCentre, String companyId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending());
        Page<Client> clientPage = repo.findIndividualClientsByLetsWorkCentre(letsWorkCentre, companyId, pageable);
        return buildPaginatedResponse(clientPage, page);
    }
	
	private PaginatedResponseDto buildPaginatedResponse(Page<Client> clientPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) clientPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) clientPage.getTotalElements());
        response.setTotalNumberOfPages(clientPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(clientPage.getContent());
        return response;
    }
	private String validate(ClientExcelDto dto) {
		if(dto.getBusinessCategory() == null || dto.getBusinessCategory().length() == 0) {
			return "Business Category not available for "+dto.getEmail();
		}
		
		if(dto.getClientCompanyName() == null || dto.getClientCompanyName().length() == 0) {
			return "Company Name for User not available for "+dto.getEmail();
		}
		
		if(dto.getEmail() == null || dto.getEmail().length() == 0) {
			return "Email for User not available for "+dto.getFirstName();
		}
		
		if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
			return "Letswork Center for User not available for "+dto.getEmail();
		}
		
		if(letsWorkCentreService.findByName(dto.getLetsWorkCentre(), dto.getCompanyId()) == null){
			return "Letswork Cente "+dto.getLetsWorkCentre()+" does not exist for User not available for "+dto.getEmail();
		}
		
		if(tenantService.findTenantByCompanyId(dto.getCompanyId())==null) {
			return "CompanyId "+dto.getCompanyId()+" does not exists";
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
		
		return "ok";
	}
	
	@Override
	public String uploadClientsFromExcel(MultipartFile file) {
        try {
            List<ClientExcelDto> clientsFromExcel = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, ClientExcelDto.class);
            	for(ClientExcelDto dto : clientsFromExcel) {
            		String val = validate(dto);
            		if(!val.equalsIgnoreCase("ok")) {
            			return val;
            		}
            	}
            
            List<String> responses = clientsFromExcel.stream().map(dto -> {
                Client client = Client.builder()
                        .firstName(dto.getFirstName().trim())
                        .lastName(dto.getLastName().trim())
                        .email(dto.getEmail().trim())
                        .phone(dto.getPhone().trim())
                        .clientCompanyName(dto.getClientCompanyName().trim())
                        .letsWorkCentre(dto.getLetsWorkCentre().trim())
                        .companyId(dto.getCompanyId().trim())
                        .businessCategory(dto.getBusinessCategory().trim())
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

	
	

}

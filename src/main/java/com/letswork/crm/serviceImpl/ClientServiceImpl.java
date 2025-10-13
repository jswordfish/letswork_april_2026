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

import com.letswork.crm.dtos.ClientExcelDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Client;
import com.letswork.crm.entities.ClientCompany;
import com.letswork.crm.repo.ClientCompanyRepository;
import com.letswork.crm.repo.ClientRepository;
import com.letswork.crm.service.ClientService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;





@Service
@Transactional
public class ClientServiceImpl implements ClientService {
	
	@Autowired
	ClientRepository repo;
	
	@Autowired
	ClientCompanyRepository clientCompanyRepo;
	
	private static final int PAGE_SIZE = 10;

	@Override
	public String saveOrUpdate(Client client) {
		// TODO Auto-generated method stub
		
		ClientCompany company = clientCompanyRepo.findByClientCompanyNameAndLocationAndCompanyId(client.getClientCompanyName(), client.getLocation(), client.getCompanyId());
		
		if(company == null) {
			return "No company with the name "+client.getClientCompanyName()+" exists";
		}
		
		Client client1 = repo.findByEmailAndCompanyId(client.getEmail(), client.getCompanyId());
		
		if(client1!=null) {
			
			client1.setName(client.getName());
			client1.setPhone(client.getPhone());
			client1.setEmail(client.getEmail());
			client1.setIsIndividual(client.getIsIndividual());
			client1.setCompanyId(client.getCompanyId());
			client1.setClientCompanyName(client.getClientCompanyName());
			client1.setLocation(client.getLocation());
			
			
			repo.save(client1);
			return "record updated";
			
		}
		
		else {
			repo.save(client);
			return "record saved";
		}
		
	}


	@Override
	public List<Client> findByName(String name) {
		// TODO Auto-generated method stub
		return repo.findByNameContainingIgnoreCase(name);
	}

	@Override
	public String deleteClient(Client client) {
		// TODO Auto-generated method stub
		
		Client client1 = repo.findByNameAndEmailAndCompanyId(client.getName(), client.getEmail(), client.getCompanyId());
		
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
	public List<Client> getIndividualClientsByLocation(String location, String companyId) {
		// TODO Auto-generated method stub
		return repo.findIndividualClientsByLocation(location, companyId);
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
    public PaginatedResponseDto findByName(String name, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        Page<Client> clientPage = repo.findByNameContainingIgnoreCase(name, pageable);
        return buildPaginatedResponse(clientPage, page);
    }
	
	@Override
    public PaginatedResponseDto getIndividualClients(String companyId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending());
        Page<Client> clientPage = repo.findIndividualClients(companyId, pageable);
        return buildPaginatedResponse(clientPage, page);
    }

    // ✅ Get individual clients by location
    @Override
    public PaginatedResponseDto getIndividualClientsByLocation(String location, String companyId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending());
        Page<Client> clientPage = repo.findIndividualClientsByLocation(location, companyId, pageable);
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
	
	@Override
	public String uploadClientsFromExcel(MultipartFile file) {
        try {
            List<ClientExcelDto> clientsFromExcel = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, ClientExcelDto.class);

            List<String> responses = clientsFromExcel.stream().map(dto -> {
                Client client = Client.builder()
                        .name(dto.getName())
                        .email(dto.getEmail())
                        .phone(dto.getPhone())
                        .isIndividual(dto.getIsIndividual() != null ? dto.getIsIndividual() : true)
                        .clientCompanyName(dto.getClientCompanyName())
                        .location(dto.getLocation())
                        .companyId(dto.getCompanyId())
                        .build();

                return saveOrUpdate(client);
            }).collect(Collectors.toList());

            return "Processed " + clientsFromExcel.size() + " clients successfully.\n" +
                    String.join("\n", responses);

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to process Excel file: " + e.getMessage();
        }
    }


	

}

package com.LetsWork.CRM.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.ClientCompany;
import com.LetsWork.CRM.service.ClientCompanyService;


@RestController
@CrossOrigin
public class ClientCompanyController {
	
	@Autowired
	ClientCompanyService service;
	
	@PostMapping("/create client company")
	public String createCompany(@RequestBody ClientCompany clientCompany, @RequestParam String token) {
		
		return service.saveOrUpdate(clientCompany);
		
	}
	
	@GetMapping("/get all companies")
	public List<ClientCompany> getAll(@RequestParam String token){
		
		return service.listAll();
		
	}
	
	@GetMapping("/get all companies paginated")
    public ResponseEntity<PaginatedResponseDto> getAllCompanies(
            @RequestParam(defaultValue = "0") int page, @RequestParam String token) {
        return ResponseEntity.ok(service.listAll(page));
    }

    
    @GetMapping("/companies by location paginated")
    public ResponseEntity<PaginatedResponseDto> getClientCompaniesByLocation(
            @RequestParam String location,
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.getClientCompaniesByLocation(location, companyId, page));
    }
	
	@GetMapping("/companies by location")
    public ResponseEntity<List<ClientCompany>> getClientCompaniesByLocation(@RequestParam String location, @RequestParam String companyId, @RequestParam String token) {
        return ResponseEntity.ok(service.getClientCompaniesByLocation(location, companyId));
    }
	
	@DeleteMapping("/delete company")
	public String deleteCompany(@RequestBody ClientCompany clientCompany, @RequestParam String token) {
		
		return service.deleteCompany(clientCompany);
		
	}

}

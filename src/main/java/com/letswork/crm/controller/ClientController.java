package com.letswork.crm.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Client;
import com.letswork.crm.service.ClientService;


@RestController
@CrossOrigin
@RequestMapping("/client")
public class ClientController {
	
	@Autowired
	ClientService service;
	
	@PostMapping
	public String createClient(@RequestBody Client client, @RequestParam String token) {
		
		return service.saveOrUpdate(client);
		
	}
	
	@PostMapping(value = "/upload-excel", consumes = "multipart/form-data")
	public ResponseEntity<String> uploadClientsExcel(
	        @RequestParam("file") MultipartFile file,
	        @RequestParam String token) {

	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
	    }

	    String response = service.uploadClientsFromExcel(file);
	    return ResponseEntity.ok(response);
	}
	
	
	
	
	
//	@GetMapping("/get clients by company")
//    public ResponseEntity<List<Client>> getClientsByCompany(@RequestParam String companyName, @RequestParam String companyId, @RequestParam String token) {
//        return ResponseEntity.ok(service.getClientsByCompany(companyName, companyId));
//    }
	
	@GetMapping("/clients-by-company")
	public ResponseEntity<PaginatedResponseDto> getClientsByCompany(
	        @RequestParam String companyName,
	        @RequestParam String companyId,
	        @RequestParam String token,
	        @RequestParam(defaultValue = "0") int page) {

	    PaginatedResponseDto response = service.getClientsByCompany(companyName, companyId, page);
	    return ResponseEntity.ok(response);
	}
	
//	@GetMapping("/get-individuals")
//    public ResponseEntity<List<Client>> getIndividualClients(@RequestParam String companyId, @RequestParam String token) {
//        return ResponseEntity.ok(service.getIndividualClients(companyId));
//    }
//	
//	@GetMapping("/individuals-by-LetsWorkCentre")
//    public ResponseEntity<List<Client>> getIndividualClientsByLetsWorkCentre(@RequestParam String letsWorkCentre, @RequestParam String companyId, @RequestParam String token) {
//        return ResponseEntity.ok(service.getIndividualClientsByLetsWorkCentre(letsWorkCentre, companyId));
//    }
	
	@DeleteMapping
	public String deleteClient(@RequestBody Client client, @RequestParam String token) {
		
		return service.deleteClient(client);
		
	}
	
//	@GetMapping
//	public ResponseEntity<PaginatedResponseDto> getIndividualClients(
//	        @RequestParam String companyId,
//	        @RequestParam(required = false) String email,
//	        @RequestParam(required = false) String letsWorkCentre,
//	        @RequestParam(required = false) String city,
//	        @RequestParam(required = false) String state,
//	        @RequestParam(defaultValue = "0") int page,
//	        @RequestParam String token) {
//
//	    PaginatedResponseDto response;
//
//	    if (letsWorkCentre != null && city != null && state != null && email==null) {
//	        response = service.getIndividualClientsByLetsWorkCentre(letsWorkCentre, companyId, city, state, page);
//	    }
//	    else if(email != null) {
//	    	Client client = service.getByEmail(email, companyId);
//	        response = new PaginatedResponseDto();
//	        if (client != null) {
//	            response.setRecordsFrom(1);
//	            response.setRecordsTo(1);
//	            response.setTotalNumberOfRecords(1);
//	            response.setTotalNumberOfPages(1);
//	            response.setSelectedPage(0);
//	            response.setList(Collections.singletonList(client));
//	        } else {
//	            response.setRecordsFrom(0);
//	            response.setRecordsTo(0);
//	            response.setTotalNumberOfRecords(0);
//	            response.setTotalNumberOfPages(0);
//	            response.setSelectedPage(0);
//	            response.setList(Collections.emptyList());
//	        }
//	    }
//	    else {
//	        response = service.getIndividualClients(companyId, page);
//	    }
//
//	    return ResponseEntity.ok(response);
//	}
	
	@GetMapping
	public ResponseEntity<PaginatedResponseDto> getIndividualClients(
	        @RequestParam String companyId,
	        @RequestParam(required = false) String email,
	        @RequestParam(required = false) String letsWorkCentre,
	        @RequestParam(required = false) String city,
	        @RequestParam(required = false) String state,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false, defaultValue = "id") String sortBy,
	        @RequestParam(required = false, defaultValue = "desc") String sortDir,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam String token) {

	    PaginatedResponseDto response = service.listClients(
	            companyId, letsWorkCentre, city, state, search, sortBy, sortDir, page
	    );

	    return ResponseEntity.ok(response);
	}
	
//	@GetMapping("/get-individuals")
//    public ResponseEntity<PaginatedResponseDto> getIndividualClients(
//            @RequestParam String companyId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam String token) {
//        return ResponseEntity.ok(service.getIndividualClients(companyId, page));
//    }
//
//    
//    @GetMapping("/individuals-by-LetsWorkCentre")
//    public ResponseEntity<PaginatedResponseDto> getIndividualClientsByLetsWorkCentre(
//            @RequestParam String letsWorkCentre,
//            @RequestParam String companyId,
//            @RequestParam String city,
//            @RequestParam String state,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam String token) {
//        return ResponseEntity.ok(service.getIndividualClientsByLetsWorkCentre(letsWorkCentre, companyId, city, state, page));
//    }

}

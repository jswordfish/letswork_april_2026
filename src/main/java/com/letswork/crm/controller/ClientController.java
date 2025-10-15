package com.letswork.crm.controller;

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
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Client;
import com.letswork.crm.service.ClientService;


@RestController
@CrossOrigin
public class ClientController {
	
	@Autowired
	ClientService service;
	
	@PostMapping("/create client")
	public String createClient(@RequestBody Client client, @RequestParam String token) {
		
		return service.saveOrUpdate(client);
		
	}
	
	@PostMapping(value = "/clients-upload-excel", consumes = "multipart/form-data")
	public ResponseEntity<String> uploadClientsExcel(
	        @RequestParam("file") MultipartFile file,
	        @RequestParam String token) {

	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
	    }

	    String response = service.uploadClientsFromExcel(file);
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/list of clients with same names")
	public List<Client> findByName(@RequestParam String client, @RequestParam String token){
		
		return service.findByName(client);
		
	}
	
	@GetMapping("/list of clients with same names paginated")
    public ResponseEntity<PaginatedResponseDto> findByName(
            @RequestParam String client,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.findByName(client, page));
    }
	
	@GetMapping("/get clients by company")
    public ResponseEntity<List<Client>> getClientsByCompany(@RequestParam String companyName, @RequestParam String companyId, @RequestParam String token) {
        return ResponseEntity.ok(service.getClientsByCompany(companyName, companyId));
    }
	
	@GetMapping("/get clients by company paginated")
	public ResponseEntity<PaginatedResponseDto> getClientsByCompany(
	        @RequestParam String companyName,
	        @RequestParam String companyId,
	        @RequestParam String token,
	        @RequestParam(defaultValue = "0") int page) {

	    PaginatedResponseDto response = service.getClientsByCompany(companyName, companyId, page);
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/get individuals")
    public ResponseEntity<List<Client>> getIndividualClients(@RequestParam String companyId, @RequestParam String token) {
        return ResponseEntity.ok(service.getIndividualClients(companyId));
    }
	
	@GetMapping("/individuals by LetsWorkCentre")
    public ResponseEntity<List<Client>> getIndividualClientsByLetsWorkCentre(@RequestParam String letsWorkCentre, @RequestParam String companyId, @RequestParam String token) {
        return ResponseEntity.ok(service.getIndividualClientsByLetsWorkCentre(letsWorkCentre, companyId));
    }
	
	@DeleteMapping("/delete client")
	public String deleteClient(@RequestBody Client client, @RequestParam String token) {
		
		return service.deleteClient(client);
		
	}
	
	@GetMapping("/get individuals paginated")
    public ResponseEntity<PaginatedResponseDto> getIndividualClients(
            @RequestParam String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String token) {
        return ResponseEntity.ok(service.getIndividualClients(companyId, page));
    }

    
    @GetMapping("/individuals by LetsWorkCentre paginated")
    public ResponseEntity<PaginatedResponseDto> getIndividualClientsByLetsWorkCentre(
            @RequestParam String letsWorkCentre,
            @RequestParam String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String token) {
        return ResponseEntity.ok(service.getIndividualClientsByLetsWorkCentre(letsWorkCentre, companyId, page));
    }

}

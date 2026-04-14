package com.letswork.crm.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.LetsWorkClientPurchesedDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.UserWithCompaniesDto;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.service.LetsWorkClientService;



@RestController
@CrossOrigin
@RequestMapping("/clientCompany")
public class LetsWorkClientController {
	
	@Autowired
	LetsWorkClientService service;
	
//	@PostMapping
//	public String createCompany(@RequestBody LetsWorkClient clientCompany, @RequestParam String token) {
//		
//		return service.saveOrUpdate(clientCompany);
//		
//	}
	
	@PostMapping(
	        value = "/client",
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<String> saveOrUpdateClient(
	        @RequestPart("client") String clientJson,
	        @RequestPart(value = "aadhaar", required = false) MultipartFile aadhaar,
	        @RequestPart(value = "pan", required = false) MultipartFile pan,
	        @RequestPart(value = "tan", required = false) MultipartFile tan,
	        @RequestPart(value = "gst", required = false) MultipartFile gst,
	        @RequestParam String token
	) throws IOException {

	    LetsWorkClient client =
	            new ObjectMapper().readValue(clientJson, LetsWorkClient.class);

	    String result =
	    		service.saveOrUpdate(client, aadhaar, pan, tan, gst);

	    return ResponseEntity.ok(result);
	}
	
	@GetMapping("/with-companies")
    public ResponseEntity<UserWithCompaniesDto> getUserWithCompanies(
            @RequestParam Long userId,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                service.getUserWithCompanies(userId, companyId)
        );
    }
	
	@PostMapping(value = "/upload-excel", consumes = "multipart/form-data")
	public ResponseEntity<String> uploadClientCompanies(
	        @RequestParam("file") MultipartFile file,
	        @RequestParam String token) throws IOException {

	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body(("Please upload a valid Excel file."));
	    }

	    String responses = service.uploadClientCompanies(file);
	    return ResponseEntity.ok(responses);
	}
	
//	@GetMapping
//	public List<ClientCompany> getAll(@RequestParam String token){
//		
//		return service.listAll();
//		
//	}
	
//	@GetMapping
//    public ResponseEntity<PaginatedResponseDto> getAllCompanies(
//            @RequestParam(defaultValue = "0") int page, @RequestParam String token) {
//        return ResponseEntity.ok(service.listAll(page));
//    }
	
	@GetMapping
	public ResponseEntity<PaginatedResponseDto> getClientCompanies(
	        @RequestParam String companyId,
	        @RequestParam(required = false) String letsWorkCentre,
	        @RequestParam(required = false) String city,
	        @RequestParam(required = false) String state,
	        @RequestParam(required = false) String category,
	        @RequestParam(required = false) String subCategory,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) String sort,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam String token
	) {

	    return ResponseEntity.ok(
	            service.listClientCompanies(
	                    companyId,
	                    letsWorkCentre,
	                    city,
	                    state,
	                    category,
	                    subCategory,
	                    search,
	                    sort,
	                    page,
	                    size
	            )
	    );
	}

    
//    @GetMapping("/companies-by-LetsWorkCentre")
//    public ResponseEntity<PaginatedResponseDto> getClientCompaniesByLetsWorkCentre(
//            @RequestParam String letsWorkCentre,
//            @RequestParam String companyId,
//            @RequestParam String city,
//            @RequestParam String state,
//            @RequestParam String token,
//            @RequestParam(defaultValue = "0") int page) {
//        return ResponseEntity.ok(service.getClientCompaniesByLetsWorkCentre(letsWorkCentre, companyId, city, state, page));
//    }
	
//	@GetMapping("/companies by LetsWorkCentre")
//    public ResponseEntity<List<ClientCompany>> getClientCompaniesByLetsWorkCentre(@RequestParam String letsWorkCentre, @RequestParam String companyId, @RequestParam String token) {
//        return ResponseEntity.ok(service.getClientCompaniesByLetsWorkCentre(letsWorkCentre, companyId));
//    }
	
	@DeleteMapping
	public String deleteCompany(@RequestBody LetsWorkClient clientCompany, @RequestParam String token) {
		
		return service.deleteCompany(clientCompany);
		
	}
	
	@GetMapping("/getLetsWorkClientPurchesed")
	public ResponseEntity<LetsWorkClientPurchesedDto> getWorkClientPurchesed(@RequestParam String token,
			@RequestParam(required = false) Long clientId) {
		return ResponseEntity.ok(service.getLetsWorkClientPurchesed(clientId));
	}

}

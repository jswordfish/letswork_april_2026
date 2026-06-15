package com.letswork.crm.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.enums.LeadQuality;
import com.letswork.crm.enums.LeadStatus;
import com.letswork.crm.enums.Source;
import com.letswork.crm.service.LeadService;

@RestController
@CrossOrigin
@RequestMapping("/lead")
public class LeadController {
	
	@Autowired
	LeadService service;
	
	@PostMapping
	public ResponseEntity<Lead> saveOrUpdate(
	        @RequestBody Lead lead,
	        @RequestParam String token
	){
		return ResponseEntity.ok(service.saveOrUpdate(lead));
	}
	
	@GetMapping
	public ResponseEntity<PaginatedResponseDto> get(
	        @RequestParam String companyId,
	        @RequestParam String token,

	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String email,
	        @RequestParam(required = false) String phone,
	        @RequestParam(required = false) String clientCompanyName,
	        @RequestParam(required = false) Source source,
	        @RequestParam(required = false) String location,
	        @RequestParam(required = false) LeadStatus status,
	        @RequestParam(required = false) LeadQuality leadQuality,
	        @RequestParam(required = false) String search,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate fromDate,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate toDate,

	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {

	    return ResponseEntity.ok(
	            service.getPaginated(
	                    companyId,
	                    name,
	                    email,
	                    phone,
	                    clientCompanyName,
	                    source,
	                    location,
	                    status,
	                    leadQuality,
	                    search,
	                    fromDate,
	                    toDate,
	                    page,
	                    size
	            )
	    );
	}
	
	@PutMapping("/change-status")
	public ResponseEntity<Lead> changeStatus(
	        @RequestParam Long leadId,
	        @RequestParam String companyId,
	        @RequestParam String token,
	        @RequestParam LeadStatus status
	) {

	    return ResponseEntity.ok(
	            service.changeStatus(
	                    leadId,
	                    companyId,
	                    status
	            )
	    );
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Lead> getById(
	        @PathVariable Long id,
	        @RequestParam String companyId,
	        @RequestParam String token
	){
		return ResponseEntity.ok(service.getById(id, companyId));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(
	        @PathVariable Long id,
	        @RequestParam String companyId,
	        @RequestParam String token
	){
		
		service.delete(id, companyId);
		
		return ResponseEntity.ok("Record deleted");
	}

}

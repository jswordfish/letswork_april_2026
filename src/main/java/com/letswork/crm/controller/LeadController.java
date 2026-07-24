package com.letswork.crm.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.LeadResponseDto;
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
	
	@PostMapping(value = "/upload-excel-leads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadLeadsExcel(
	        @RequestParam("file") MultipartFile file,
	        @RequestParam String companyId,
	        @RequestParam String token) {

	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
	    }

	    try {
	        String response = service.uploadLeadsFromExcel(file, companyId);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
	    }
	}
	
	@GetMapping
	public ResponseEntity<PaginatedResponseDto> get(
	        @RequestParam String companyId,
	        @RequestParam String token,

	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String email,
	        @RequestParam(required = false) String phone,
	        @RequestParam(required = false) String clientCompanyName,
	        @RequestParam(required = false) List<Source> sources,
	        @RequestParam(required = false) String location,
	        @RequestParam(required = false) List<LeadStatus> statuses,
	        @RequestParam(required = false) List<LeadQuality> leadQualities,
	        @RequestParam(required = false) List<String> letsWorkCentres,
	        @RequestParam(required = false) String city,
	        @RequestParam(required = false) String state,
	        @RequestParam(required = false) String solution,
	        @RequestParam(required = false) String search,
	        
	        @RequestParam(required = false) Long userId,

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
	    		        sources,
	    		        location,
	    		        statuses,
	    		        leadQualities,
	    		        letsWorkCentres,
	    		        city,
	    		        state,
	    		        solution,
	    		        search,
	    		        userId,
	    		        fromDate,
	    		        toDate,
	    		        page,
	    		        size
	    		)
	    );
	}
	
	@GetMapping("/export")
	public void export(
			@RequestParam String companyId,
	        @RequestParam String token,

	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String email,
	        @RequestParam(required = false) String phone,
	        @RequestParam(required = false) String clientCompanyName,
	        @RequestParam(required = false) List<Source> sources,
	        @RequestParam(required = false) String location,
	        @RequestParam(required = false) List<LeadStatus> statuses,
	        @RequestParam(required = false) List<LeadQuality> leadQualities,
	        @RequestParam(required = false) List<String> letsWorkCentres,
	        @RequestParam(required = false) String city,
	        @RequestParam(required = false) String state,
	        @RequestParam(required = false) String solution,
	        @RequestParam(required = false) String search,
	        
	        @RequestParam(required = false) Long userId,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate fromDate,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate toDate,

	        HttpServletResponse response
	) throws IOException {

	    service.exportToExcel(
	    		companyId,
		        name,
		        email,
		        phone,
		        clientCompanyName,
		        sources,
		        location,
		        statuses,
		        leadQualities,
		        letsWorkCentres,
		        city,
		        state,
		        solution,
		        search,
		        userId,
		        fromDate,
		        toDate,
	            response
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
	public ResponseEntity<LeadResponseDto> getById(
	        @PathVariable Long id,
	        @RequestParam String companyId,
	        @RequestParam String token
	){
	    return ResponseEntity.ok(
	            service.getById(id, companyId)
	    );
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

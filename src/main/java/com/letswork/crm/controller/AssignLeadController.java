package com.letswork.crm.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.BulkAssignLeadRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.AssignLead;
import com.letswork.crm.service.AssignLeadService;

@RestController
@CrossOrigin
@RequestMapping("/AssingLead")
public class AssignLeadController {
	
	@Autowired
	AssignLeadService service;
	
	@PostMapping
	public ResponseEntity<AssignLead> createActivity(
	        @RequestBody AssignLead assignLead,
	        @RequestParam String token
	) {

	    return ResponseEntity.ok(
	            service.createAssign(assignLead)
	    );
	}
	
	@PostMapping("/bulk-assign")
	public ResponseEntity<String> bulkAssign(
	        @RequestBody BulkAssignLeadRequest request,
	        @RequestParam String token
	) {

	    return ResponseEntity.ok(
	            service.bulkAssign(
	                    request.getCompanyId(),
	                    request.getUserId(),
	                    request.getLeadIds()
	            )
	    );
	}
	
	@GetMapping
	public ResponseEntity<PaginatedResponseDto> get(
	        @RequestParam String companyId,
	        @RequestParam String token,

	        @RequestParam(required = false)
	        Long leadId,

	        @RequestParam(required = false)
	        Long userId,

	        @RequestParam(required = false)
	        String search,

	        @RequestParam(required = false)
	        @DateTimeFormat(
	                iso = DateTimeFormat.ISO.DATE
	        )
	        LocalDate fromDate,

	        @RequestParam(required = false)
	        @DateTimeFormat(
	                iso = DateTimeFormat.ISO.DATE
	        )
	        LocalDate toDate,

	        @RequestParam(defaultValue = "0")
	        int page,

	        @RequestParam(defaultValue = "10")
	        int size
	) {

	    return ResponseEntity.ok(
	            service.getPaginated(
	                    companyId,
	                    leadId,
	                    userId,
	                    search,
	                    fromDate,
	                    toDate,
	                    page,
	                    size
	            )
	    );
	}

}

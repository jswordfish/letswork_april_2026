package com.letswork.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.BulkSeatAssignmentRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ClientCompanySeatMapping;
import com.letswork.crm.service.ClientCompanySeatMappingService;

@RestController
@CrossOrigin
@RequestMapping("/client-company-seat-mapping")
public class ClientCompanySeatMappingController {
	
	@Autowired
	ClientCompanySeatMappingService service;
	
	@PostMapping
    public ResponseEntity<ClientCompanySeatMapping> createOrUpdate(@RequestBody ClientCompanySeatMapping mapping, @RequestParam String token) {
        return ResponseEntity.ok(service.saveOrUpdate(mapping));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> listByLetsWorkCentre(
            @RequestParam String companyId,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String token) {

        PaginatedResponseDto response = service.listByLetsWorkCentre(companyId, letsWorkCentre, city, state, page);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/assign-multiple")
    public ResponseEntity<?> assignMultipleSeats(@RequestBody BulkSeatAssignmentRequest request, @RequestParam String token) {
        try {
            List<ClientCompanySeatMapping> saved = service.assignMultipleSeats(request);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/list-by-clientCompany")
    public ResponseEntity<PaginatedResponseDto> listForSpecificClient(
            @RequestParam String clientCompanyName,
            @RequestParam String letsWorkCentre,
            @RequestParam String companyId,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.listForSpecificClient(clientCompanyName, letsWorkCentre, companyId, city, state, page));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMapping(@RequestParam Long id, @RequestParam String token) {
        return ResponseEntity.ok(service.deleteMapping(id));
    }

}

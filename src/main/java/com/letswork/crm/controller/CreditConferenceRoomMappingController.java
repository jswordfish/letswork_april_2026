package com.letswork.crm.controller;

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

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.CreditConferenceRoomMapping;
import com.LetsWork.CRM.service.CreditConferenceRoomMappingService;

@RestController
@RequestMapping("/api/confroom-credits")
@CrossOrigin
public class CreditConferenceRoomMappingController {
	
	@Autowired
    private CreditConferenceRoomMappingService mappingService;

    @PostMapping("/save credit mapping")
    public ResponseEntity<CreditConferenceRoomMapping> saveOrUpdateMapping(@RequestBody CreditConferenceRoomMapping mapping, @RequestParam String token) {
        // Assume companyId is included in the request body (Mapping object)
        CreditConferenceRoomMapping savedMapping = mappingService.saveOrUpdate(mapping);
        return ResponseEntity.ok(savedMapping);
    }

    @GetMapping("/get credit mapping")
    public ResponseEntity<PaginatedResponseDto> listMappings(
        @RequestParam String companyId, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam String token) {
            
        PaginatedResponseDto response = mappingService.listAll(companyId, page, size);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete by id")
    public ResponseEntity<String> deleteMapping(@RequestParam Long id, @RequestParam String token) {
        mappingService.deleteById(id);
        return ResponseEntity.ok("Conference room credit mapping deleted successfully.");
    }

}

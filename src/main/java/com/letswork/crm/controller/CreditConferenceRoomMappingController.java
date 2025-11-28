package com.letswork.crm.controller;

import java.util.List;

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

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.CreditConferenceRoomMapping;
import com.letswork.crm.service.CreditConferenceRoomMappingService;

@RestController
@RequestMapping("/api/confroom-credits")
@CrossOrigin
public class CreditConferenceRoomMappingController {
	
	@Autowired
    private CreditConferenceRoomMappingService mappingService;

    @PostMapping
    public ResponseEntity<CreditConferenceRoomMapping> saveOrUpdateMapping(@RequestBody CreditConferenceRoomMapping mapping, @RequestParam String token) {
        // Assume companyId is included in the request body (Mapping object)
        CreditConferenceRoomMapping savedMapping = mappingService.saveOrUpdate(mapping);
        return ResponseEntity.ok(savedMapping);
    }

    @GetMapping("/all")
    public ResponseEntity<PaginatedResponseDto> listMappings(
        @RequestParam String companyId, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam String token) {
            
        PaginatedResponseDto response = mappingService.listAll(companyId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/by-centre")
    public List<CreditConferenceRoomMapping> listByCentre(@RequestParam String roomName,
    		@RequestParam String letsWorkCentre,
    		@RequestParam String city,
    		@RequestParam String state,
    		@RequestParam String companyId,
    		@RequestParam String token){
    	
    	return mappingService.listByCentre(roomName, letsWorkCentre, companyId, city, state);
    	
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMapping(@RequestParam Long id, @RequestParam String token) {
        mappingService.deleteById(id);
        return ResponseEntity.ok("Conference room credit mapping deleted successfully.");
    }

}

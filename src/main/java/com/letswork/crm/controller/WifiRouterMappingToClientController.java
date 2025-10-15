package com.letswork.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.WifiRouterMappingToClient;
import com.letswork.crm.service.WifiRouterMappingToClientService;

@RestController
@CrossOrigin
public class WifiRouterMappingToClientController {
	
	@Autowired
	WifiRouterMappingToClientService service;
	
	@PostMapping("/create mapping")
	public String createOrUpdate(@RequestBody WifiRouterMappingToClient mapping, @RequestParam String token) {
		
		return service.saveOrUpdate(mapping);
		
	}
	
	@DeleteMapping("/delete mapping")
	public String deleteMapping(@RequestBody WifiRouterMappingToClient mapping, @RequestParam String token) {
		
		return service.deleteMapping(mapping);
		
	}
	
	@GetMapping("/get clients by wifi")
    public PaginatedResponseDto getClientsByWifi(@RequestParam String wifiName,
                                                 @RequestParam String letsWorkCentre,
                                                 @RequestParam String companyId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam String token) {
        return service.getClientsByWifi(wifiName, letsWorkCentre, companyId, page);
    }

    
    @GetMapping("/get routers by client")
    public PaginatedResponseDto getRoutersByClient(@RequestParam String clientName,
                                                   @RequestParam String clientEmail,
                                                   @RequestParam String companyId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam String token) {
        return service.getRoutersByClient(clientName, clientEmail, companyId, page);
    }

}

package com.LetsWork.CRM.controller;

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

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.Location;
import com.LetsWork.CRM.service.LocationService;


@RestController
@CrossOrigin
public class LocationController {
	
	@Autowired
	LocationService service;
	
	@PostMapping("/create location")
	public String createOrUpdate(@RequestBody Location location, @RequestParam String token) {
		
		return service.saveOrUpdate(location);
		
	}
	
	@GetMapping("/fetch all locations")
	public List<Location> fetchAll(@RequestParam String token){
		return service.findAll();
	}
	
	@GetMapping("/fetch all locations paginated")
    public ResponseEntity<PaginatedResponseDto> getAllLocations(
            @RequestParam(defaultValue = "0") int page, @RequestParam String token) {
        return ResponseEntity.ok(service.getAllLocations(page));
    }
	
	@DeleteMapping("/delete location")
	public String deleteLocation(@RequestBody Location location, @RequestParam String token) {
		
		return service.deleteLocation(location);
		
	}
	

}

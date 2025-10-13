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
import com.letswork.crm.entities.Location;
import com.letswork.crm.service.LocationService;


@RestController
@CrossOrigin
public class LocationController {
	
	@Autowired
	LocationService service;
	
	@PostMapping("/create location")
	public String createOrUpdate(@RequestBody Location location, @RequestParam String token) {
		
		return service.saveOrUpdate(location);
		
	}
	
	@PostMapping(
		    value = "/upload-excel-location",
		    consumes = "multipart/form-data"
		)
    public ResponseEntity<String> uploadLocationsExcel(@RequestParam("file") MultipartFile file, @RequestParam String token) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
        }

        String response = service.uploadLocationsFromExcel(file);
        return ResponseEntity.ok(response);
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

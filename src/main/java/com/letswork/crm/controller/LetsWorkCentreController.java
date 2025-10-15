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
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.service.LetsWorkCentreService;


@RestController
@CrossOrigin
public class LetsWorkCentreController {
	
	@Autowired
	LetsWorkCentreService service;
	
	@PostMapping("/create LetsWorkCentre")
	public String createOrUpdate(@RequestBody LetsWorkCentre letsWorkCentre, @RequestParam String token) {
		
		return service.saveOrUpdate(letsWorkCentre);
		
	}
	
	@PostMapping(
		    value = "/upload-excel-LetsWorkCentre",
		    consumes = "multipart/form-data"
		)
    public ResponseEntity<String> uploadLetsWorkCentresExcel(@RequestParam("file") MultipartFile file, @RequestParam String token) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
        }

        String response = service.uploadLetsWorkCentresFromExcel(file);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/fetch all LetsWorkCentres")
	public List<LetsWorkCentre> fetchAll(@RequestParam String token){
		return service.findAll();
	}
	
	@GetMapping("/fetch all LetsWorkCentres paginated")
    public ResponseEntity<PaginatedResponseDto> getAllLetsWorkCentres(
            @RequestParam(defaultValue = "0") int page, @RequestParam String token) {
        return ResponseEntity.ok(service.getAllLetsWorkCentres(page));
    }
	
	@DeleteMapping("/delete LetsWorkCentre")
	public String deleteLetsWorkCentre(@RequestBody LetsWorkCentre letsWorkCentre, @RequestParam String token) {
		
		return service.deleteLetsWorkCentre(letsWorkCentre);
		
	}
	

}

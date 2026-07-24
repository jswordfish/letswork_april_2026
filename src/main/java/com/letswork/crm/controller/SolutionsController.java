package com.letswork.crm.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.SolutionsDto;
import com.letswork.crm.entities.Solutions;
import com.letswork.crm.service.SolutionsService;

@RestController
@CrossOrigin
public class SolutionsController {
	
	@Autowired
    private SolutionsService solutionsService;

	@PostMapping(
	        value = "/solution",
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<String> saveOrUpdateSolution(
	        @RequestPart("solution") String solutionJson,
	        @RequestPart(value = "image", required = false) MultipartFile image,
	        @RequestParam String token
	) throws IOException {
		
		
		SolutionsDto dto =
	            new ObjectMapper().readValue(
	                    solutionJson,
	                    SolutionsDto.class
	            );

		try {
	        String result = solutionsService.saveOrUpdate(dto, image);
	        return ResponseEntity.ok(result);
	    } catch (DataIntegrityViolationException ex) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "This solution Type for this centre already exists."
	        );
	    }
	}
	
	@GetMapping("/solution")
	public ResponseEntity<?> getSolutions(
	        @RequestParam String companyId,
	        @RequestParam(required = false) String letsWorkCentre,
	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String search,
	        @RequestParam String token,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {

	    if (name != null && letsWorkCentre != null) {
	        Solutions solution = solutionsService
	                .findByNameAndLetsWorkCentreAndCompanyId(
	                        name,
	                        letsWorkCentre,
	                        companyId
	                );
	        return ResponseEntity.ok(solution);
	    }

	    return ResponseEntity.ok(
	            solutionsService.getPaginated(
	                    companyId,
	                    letsWorkCentre,
	                    search,
	                    page,
	                    size
	            )
	    );
	}

}

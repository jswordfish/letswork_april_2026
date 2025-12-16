package com.letswork.crm.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
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

	    Solutions solution =
	            new ObjectMapper().readValue(solutionJson, Solutions.class);

	    String result =
	            solutionsService.saveOrUpdate(solution, image);

	    return ResponseEntity.ok(result);
	}

}

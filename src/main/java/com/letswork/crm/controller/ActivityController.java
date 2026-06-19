package com.letswork.crm.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.Activity;
import com.letswork.crm.enums.ActionType;
import com.letswork.crm.service.ActivityService;

@RestController
@CrossOrigin
@RequestMapping("/activity")
public class ActivityController {
	
	@Autowired
	ActivityService service;
	
	@PostMapping
	public ResponseEntity<Activity> createActivity(
	        @RequestBody Activity activity,
	        @RequestParam String token
	) {

	    return ResponseEntity.ok(
	            service.createActivity(activity)
	    );
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Activity> getById(
	        @PathVariable Long id,
	        @RequestParam String companyId,
	        @RequestParam String token
	) {

	    return ResponseEntity.ok(
	            service.getById(id, companyId)
	    );
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(
	        @PathVariable Long id,
	        @RequestParam String companyId,
	        @RequestParam String token
	) {

	    service.delete(id, companyId);

	    return ResponseEntity.ok(
	            "Activity deleted successfully"
	    );
	}
	
	@GetMapping
	public ResponseEntity<List<Activity>> get(
	        @RequestParam String companyId,
	        @RequestParam String token,

	        @RequestParam(required = false)
	        Long leadId,

	        @RequestParam(required = false)
	        String header,

	        @RequestParam(required = false)
	        ActionType actionType,

	        @RequestParam(required = false)
	        String search,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate fromDate,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate toDate
	) {

	    return ResponseEntity.ok(
	            service.get(
	                    companyId,
	                    leadId,
	                    header,
	                    actionType,
	                    search,
	                    fromDate,
	                    toDate
	            )
	    );
	}

}

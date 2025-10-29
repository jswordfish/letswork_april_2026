package com.letswork.crm.controller;

import java.time.LocalDate;
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
import com.letswork.crm.entities.Visitor;
import com.letswork.crm.service.VisitorService;


@RestController
@CrossOrigin
@RequestMapping("/visitor")
public class VisitorController {
	
	@Autowired
	VisitorService service;
	
	@PostMapping
	public String createOrUpdate(@RequestBody Visitor visitor, @RequestParam String token) {
		
		return service.saveOrUpdate(visitor);
		
	}
	
//	@GetMapping("/view visitor by date")
//	public List<Visitor> viewByDate(@RequestParam LocalDate visitDate, @RequestParam String token){
//		
//		return service.viewByDate(visitDate);
//		
//	}
	
	@GetMapping
    public ResponseEntity<PaginatedResponseDto> viewByDate(
            @RequestParam LocalDate visitDate,
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.viewByDate(visitDate, companyId, page));
    }
	
	@DeleteMapping
	public String deleteVisitor(@RequestBody Visitor visitor, @RequestParam String token) {
		
		return service.deleteVisitor(visitor);
		
	}

}

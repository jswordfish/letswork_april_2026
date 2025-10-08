package com.LetsWork.CRM.controller;

import java.time.LocalDate;
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
import com.LetsWork.CRM.entities.Visitor;
import com.LetsWork.CRM.service.VisitorService;


@RestController
@CrossOrigin
public class VisitorController {
	
	@Autowired
	VisitorService service;
	
	@PostMapping("/create visitor")
	public String createOrUpdate(@RequestBody Visitor visitor, @RequestParam String token) {
		
		return service.saveOrUpdate(visitor);
		
	}
	
	@GetMapping("/view visitor by date")
	public List<Visitor> viewByDate(@RequestParam LocalDate visitDate, @RequestParam String token){
		
		return service.viewByDate(visitDate);
		
	}
	
	@GetMapping("/view visitor by date paginated")
    public ResponseEntity<PaginatedResponseDto> viewByDate(
            @RequestParam LocalDate visitDate,
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.viewByDate(visitDate, companyId, page));
    }
	
	@DeleteMapping("/Delete visitor")
	public String deleteVisitor(@RequestBody Visitor visitor, @RequestParam String token) {
		
		return service.deleteVisitor(visitor);
		
	}

}

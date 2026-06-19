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

import com.letswork.crm.dtos.CommentResponseDto;
import com.letswork.crm.entities.Comment;
import com.letswork.crm.service.CommentService;

@RestController
@CrossOrigin
@RequestMapping("/comment")
public class CommentController {
	
	@Autowired
	CommentService service;
	
	@PostMapping
	public ResponseEntity<Comment> saveOrUpdate(
	        @RequestBody Comment comment,
	        @RequestParam String token
	) {

	    return ResponseEntity.ok(
	            service.saveOrUpdate(comment));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Comment> getById(
	        @PathVariable Long id,
	        @RequestParam String companyId,
	        @RequestParam String token
	) {

	    return ResponseEntity.ok(
	            service.getById(id, companyId));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(
	        @PathVariable Long id,
	        @RequestParam String companyId,
	        @RequestParam String token
	) {

	    service.delete(id, companyId);

	    return ResponseEntity.ok(
	            "Comment deleted successfully");
	}
	
	@GetMapping
	public ResponseEntity<List<CommentResponseDto>> get(
	        @RequestParam String companyId,
	        @RequestParam String token,

	        @RequestParam(required = false)
	        Long leadId,

	        @RequestParam(required = false)
	        String comment,

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
	                    comment,
	                    search,
	                    fromDate,
	                    toDate
	            )
	    );
	}

}

package com.letswork.crm.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.entities.User;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.service.UserService;


@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
	
	
	ObjectMapper mapper = new ObjectMapper();
	
	static String VALIDATION_OK = "ok";
	
	
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-mm-DD hh:mm:ss");
	
	@Autowired
	UserService service;
	
	@Autowired
	UserRepo repo;
	
	@PostMapping
	public ResponseEntity<User> createOrUpdateUser(@RequestBody User user, @RequestParam String token) {
		
		try {
		User res = service.saveOrUpdate(user);
		
		return ResponseEntity.ok(res);
		}catch (DataIntegrityViolationException ex) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "This email for this user already exists."
	        );
	    }
		
	}
	
	@PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadUsersExcel(
	        @RequestParam("file") MultipartFile file,
	        @RequestParam String companyId,
	        @RequestParam String token) {

	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
	    }

	    try {
	        String response = service.uploadUsersFromExcel(file, companyId);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        return ResponseEntity.internalServerError().body(("Error: " + e.getMessage()));
	    }
	}
	
	@DeleteMapping
	public String deleteUser(@RequestBody User user, @RequestParam String token) {
		
		User user2 = service.findByEmail(user.getEmail(), user.getCompanyId());
		
		if(user2!=null) {
			repo.delete(user2);
			return "User deleted";
		}
	
		else return "User does not exists";
	}
	
	 
//	@GetMapping
//	public List<User> fetchUsers(){
//		
//		return service.findAll();
//		
//	}
	
	@GetMapping
	public ResponseEntity<Page<User>> getUsers(
	        @RequestParam String companyId,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) String department,
	        @RequestParam(required = false) String roleOrDesig,
	        @RequestParam(required = false) String letsWorkCentre,
	        @RequestParam(required = false) String city,
	        @RequestParam(required = false) String state,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String sort,
	        @RequestParam String token
	) {

	    String sortField = "id";
	    String sortDirection = "desc";

	    if (sort != null && sort.contains("=")) {
	        String[] parts = sort.split("=");
	        sortField = parts[0];
	        sortDirection = parts.length > 1 ? parts[1] : "asc";
	    }

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            sortDirection.equalsIgnoreCase("asc")
	                    ? Sort.by(sortField).ascending()
	                    : Sort.by(sortField).descending()
	    );

	    Page<User> users = service.getUsers(
	            companyId,
	            search,
	            department,
	            roleOrDesig,
	            letsWorkCentre,
	            city,
	            state,
	            pageable
	    );

	    return ResponseEntity.ok(users);
	}

}

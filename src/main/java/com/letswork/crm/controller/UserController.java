package com.letswork.crm.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.entities.User;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.service.UserService;


@RestController
@CrossOrigin
public class UserController {
	
	
	ObjectMapper mapper = new ObjectMapper();
	
	static String VALIDATION_OK = "ok";
	
	
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-mm-DD hh:mm:ss");
	
	@Autowired
	UserService service;
	
	@Autowired
	UserRepo repo;
	
	@PostMapping("/create_user")
	public void createOrUpdateUser(@RequestBody User user, @RequestParam String token) {
		
		service.saveOrUpdate(user);
		
	}
	
	@PostMapping(value = "/upload-users-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
	
	@DeleteMapping("/delete user")
	public String deleteUser(@RequestBody User user, @RequestParam String token) {
		
		User user2 = service.findByEmail(user.getEmail(), user.getCompanyId());
		
		if(user2!=null) {
			repo.delete(user2);
			return "User deleted";
		}
	
		else return "User does not exists";
	}
	
	 
	@GetMapping("/fetch Users")
	public List<User> fetchUsers(){
		
		return service.findAll();
		
	}
	

}

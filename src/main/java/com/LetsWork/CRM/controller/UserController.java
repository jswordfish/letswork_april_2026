package com.LetsWork.CRM.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LetsWork.CRM.entities.User;
import com.LetsWork.CRM.repo.UserRepo;
import com.LetsWork.CRM.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;


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

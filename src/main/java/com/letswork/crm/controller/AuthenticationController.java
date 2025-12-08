package com.letswork.crm.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.AuthenticationResponse;
import com.letswork.crm.entities.User;
import com.letswork.crm.service.UserService;
import com.letswork.crm.util.TokenService2;



@RestController
@CrossOrigin
public class AuthenticationController {
	
	@Autowired
	UserService userService;
	
	TokenService2 tokenService = new TokenService2();
	
	@CrossOrigin
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody User user, @RequestParam(required = false) String superFlag)
			throws Exception {

		Objects.requireNonNull(user);
		Objects.requireNonNull(user.getEmail());
		Objects.requireNonNull(user.getPassword());
		Objects.requireNonNull(user.getCompanyId());
		User u =  userService.findByEmail(user.getEmail(), user.getCompanyId());
		
		if(superFlag != null && superFlag.equals("yes")) {
			if(!user.getEmail().equals("admin@letswork.com")) {
				return ResponseEntity.ok("failure : Not a Super Admin User");
			}
		}
		else {
			if(user.getEmail().equals("admin@letswork.com")) {
				return ResponseEntity.ok("failure : Not a Super Admin login Page");
			}
		}
		
			if(u == null) {
				if(user.getEmail().equals("admin@letswork.com") && user.getPassword().equals("12345")) {
					String token =  tokenService.generateToken("SUPER_ADMIN", user.getEmail());
					return ResponseEntity.ok(token);
				}
				
				return ResponseEntity.ok("failure");
			}
			else {
				//System.out.println("passed pwd "+user.getPassword()+" db pwd "+u.getPassword());
					if(user.getPassword().equals(u.getPassword())) {
						String token =  tokenService.generateToken(u.getOrgHierarchy().getRoleOrDesig(), u.getEmail());
						return ResponseEntity.ok(token);
					}
					else {
						return ResponseEntity.ok("failure : Wrong PAssword");
					}
				
				
			}
		
	} 
	
	@CrossOrigin
	@RequestMapping(value = "/token2", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken2(@RequestBody User user, @RequestParam(required = false) String superFlag)
			throws Exception {

		Objects.requireNonNull(user);
		Objects.requireNonNull(user.getEmail());
		Objects.requireNonNull(user.getPassword());
		Objects.requireNonNull(user.getCompanyId());
		User u =  userService.findByEmail(user.getEmail(), user.getCompanyId());
		AuthenticationResponse response = new AuthenticationResponse();
		
		if(superFlag != null && superFlag.equals("yes")) {
			if(!user.getEmail().equals("admin@letswork.com")) {
				return ResponseEntity.ok("failure : Not a Super Admin User");
			}
		}
		else {
			if(user.getEmail().equals("admin@letswork.com")) {
				return ResponseEntity.ok("failure : Not a Super Admin login Page");
			}
		}
		
			if(u == null) {
				if(user.getEmail().equals("admin@letswork.com") && user.getPassword().equals("12345")) {
					String token =  tokenService.generateToken("SUPER_ADMIN", user.getEmail());
					response.setToken(token);
					return ResponseEntity.ok(response);
				}
				
				return ResponseEntity.ok("failure");
			}
			else {
				//System.out.println("passed pwd "+user.getPassword()+" db pwd "+u.getPassword());
					if(user.getPassword().equals(u.getPassword()) && superFlag.equals("yes")) {
						String token =  tokenService.generateToken(u.getOrgHierarchy().getRoleOrDesig(), u.getEmail());
						response.setToken(token);
						return ResponseEntity.ok(response);
					}
					else {
						return ResponseEntity.ok("failure : Wrong PAssword");
					}
				
				
			}
		
	} 

}
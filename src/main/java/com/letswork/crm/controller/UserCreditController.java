package com.letswork.crm.controller;

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
import com.letswork.crm.entities.UserCredit;
import com.letswork.crm.service.UserCreditService;

@RestController
@CrossOrigin
@RequestMapping("/userCredits")
public class UserCreditController {
	
	@Autowired
    private UserCreditService userCreditService;

    @PostMapping
    public ResponseEntity<UserCredit> saveOrUpdateUserCredit(@RequestBody UserCredit userCredit, @RequestParam String token) {
        // Assume companyId is included in the request body (UserCredit object)
        UserCredit savedCredit = userCreditService.saveOrUpdate(userCredit);
        return ResponseEntity.ok(savedCredit);
    }

    @GetMapping("/list-user-credits")
    public ResponseEntity<UserCredit> getUserCreditsByEmail(
        @RequestParam String email,
        @RequestParam String companyId,
        @RequestParam String token) { 
        
        UserCredit userCredit = userCreditService.getByUserEmail(email, companyId);
        if (userCredit == null) {
             return ResponseEntity.notFound().build(); 
        }
        return ResponseEntity.ok(userCredit);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> listAllUserCredits(
        @RequestParam String companyId, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam String token) {
            
        PaginatedResponseDto response = userCreditService.listAll(companyId, page, size);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUserCredit(@RequestParam Long id, @RequestParam String token) {
        userCreditService.deleteById(id);
        return ResponseEntity.ok("User credit entry deleted successfully.");
    }

}

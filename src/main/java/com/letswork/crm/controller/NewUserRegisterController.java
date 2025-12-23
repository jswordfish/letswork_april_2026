package com.letswork.crm.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.util.TokenService2;

@RestController
@CrossOrigin
public class NewUserRegisterController {

    @Autowired
    private NewUserRegisterService service;
    
    TokenService2 tokenService = new TokenService2();

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody NewUserRegister user) {

        NewUserRegister saved = service.save(user);

        String token =
                tokenService.generateToken("App User", saved.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", saved);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/profile-image")
    public ResponseEntity<Map<String, Object>> uploadProfileImage(
            @RequestParam String companyId,
            @RequestParam String email,
            @RequestPart MultipartFile image,
            @RequestParam String token) {

        NewUserRegister updatedUser = service.updateProfileImage(companyId, email, image);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile image updated successfully");
        response.put("user", updatedUser);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/NewUsers-get")
    public ResponseEntity<?> getUsers(
            @RequestParam String companyId,
            @RequestParam(required = false) String email
            ) {

        if (email != null && !email.isEmpty()) {

            return ResponseEntity.ok(service.getByEmailAndCompanyId(email,companyId));
        }

        return ResponseEntity.ok(
                service.getAllByCompanyId(companyId)
        );
    }
}

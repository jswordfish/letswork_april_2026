package com.letswork.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.service.NewUserRegisterService;

@RestController
@CrossOrigin
@RequestMapping("/api/NewUsers")
public class NewUserRegisterController {

    @Autowired
    private NewUserRegisterService service;

    @PostMapping
    public ResponseEntity<NewUserRegister> saveOrUpdate(
            @RequestBody NewUserRegister user,
            @RequestParam String token) {

        NewUserRegister saved =service.saveOrUpdate(user);

        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam String companyId,
            @RequestParam(required = false) String email,
            @RequestParam String token) {

        if (email != null && !email.isEmpty()) {

            return ResponseEntity.ok(service.getByEmailAndCompanyId(email,companyId));
        }

        return ResponseEntity.ok(
                service.getAllByCompanyId(companyId)
        );
    }
}

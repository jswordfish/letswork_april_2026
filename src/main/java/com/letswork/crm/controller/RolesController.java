package com.letswork.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.Rbac_entity;
import com.letswork.crm.service.RolesService;

@RestController
@RequestMapping("/roles")
@CrossOrigin
public class RolesController {

    @Autowired
    private RolesService service;

    @PostMapping
    public ResponseEntity<Rbac_entity> saveOrUpdate(@RequestBody Rbac_entity role, @RequestParam String token) {
        Rbac_entity saved = service.saveOrUpdate(role);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Rbac_entity>> list(@RequestParam String companyId, @RequestParam String token) {
        return ResponseEntity.ok(service.listByCompanyId(companyId));
    }
}

package com.letswork.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.SolutionType;
import com.letswork.crm.service.SolutionTypeService;

@RestController
@RequestMapping("/solution-type")
public class SolutionTypeController {

    @Autowired
    private SolutionTypeService service;

    @PostMapping("/save")
    public ResponseEntity<SolutionType> save(
            @RequestBody SolutionType solutionType,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        solutionType.setCompanyId(companyId);
        return ResponseEntity.ok(service.saveOrUpdate(solutionType));
    }

    @GetMapping("/get")
    public ResponseEntity<List<SolutionType>> get(
    		@RequestParam String companyId,
    		@RequestParam String token,
            @RequestParam(required = false) String name
    ) {
        return ResponseEntity.ok(service.getSolutionTypes(companyId, name));
    }
}

package com.letswork.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.letswork.crm.entities.Amenities;
import com.letswork.crm.enums.AmenityType;
import com.letswork.crm.service.AmenitiesService;

@CrossOrigin
@RestController
@RequestMapping("/api/amenities")
public class AmenitiesController {

    @Autowired
    private AmenitiesService service;

    // Create or Update
    @PostMapping
    public ResponseEntity<Amenities> saveOrUpdate(@RequestBody Amenities amenities) {
        Amenities saved = service.saveOrUpdate(amenities);
        return ResponseEntity.ok(saved);
    }

    // List by type
    @GetMapping
    public ResponseEntity<List<Amenities>> list(
            @RequestParam String companyId,
            @RequestParam AmenityType type) {

        return ResponseEntity.ok(service.listByAmenityType(companyId, type));
    }

    // Delete
    @DeleteMapping
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.deleteAmenity(id);
        return ResponseEntity.ok("Deleted Successfully");
    }
}

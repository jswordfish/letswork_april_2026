package com.letswork.crm.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    @PostMapping(
            value = "/amenities",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Amenities> saveOrUpdateAmenity(
            @RequestPart("amenities") String amenitiesJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam String token
    ) throws IOException {

        Amenities amenities =
                new ObjectMapper().readValue(
                        amenitiesJson,
                        Amenities.class
                );

        return ResponseEntity.ok(
                service.saveOrUpdate(amenities, image)
        );
    }

    // List by type
    @GetMapping
    public ResponseEntity<List<Amenities>> list(
            @RequestParam String companyId,
            @RequestParam(required=false) AmenityType type,
            @RequestParam String token) {
    	
    	if(type!=null) {
        return ResponseEntity.ok(service.listByAmenityType(companyId, type));
    	}
    	else return ResponseEntity.ok(service.listByCompanyId(companyId));
    }

    // Delete
    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam Long id, @RequestParam String token) {
        service.deleteAmenity(id);
        return ResponseEntity.ok("Deleted Successfully");
    }
}

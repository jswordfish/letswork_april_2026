package com.letswork.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.entities.Amenities;
import com.letswork.crm.entities.ConferenceRoomAmenityMapping;
import com.letswork.crm.service.ConferenceRoomAmenityMappingService;

@RestController
@RequestMapping("/api/conference-room-amenities")
@CrossOrigin
public class ConferenceRoomAmenityMappingController {

    @Autowired
    private ConferenceRoomAmenityMappingService service;

    // Assign amenity to room
    @PostMapping("/assign")
    public ResponseEntity<ConferenceRoomAmenityMapping> assign(
            @RequestParam Long roomId,
            @RequestParam Long amenityId) {

        return ResponseEntity.ok(service.assignAmenity(roomId, amenityId));
    }

    // List amenities for the room
    @GetMapping("/list/{roomId}")
    public ResponseEntity<List<Amenities>> list(@PathVariable Long roomId) {
        return ResponseEntity.ok(service.getAmenitiesForRoom(roomId));
    }

    // Remove mapping
    @DeleteMapping("/{mappingId}")
    public ResponseEntity<String> delete(@PathVariable Long mappingId) {
        service.removeAmenity(mappingId);
        return ResponseEntity.ok("Amenity removed from room successfully");
    }
}

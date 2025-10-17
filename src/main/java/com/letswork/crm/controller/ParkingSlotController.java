package com.letswork.crm.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ParkingSlot;
import com.letswork.crm.service.ParkingSlotService;


@RestController
@CrossOrigin
public class ParkingSlotController {
	
	@Autowired
	ParkingSlotService service;
	
	@PostMapping("/create or update parking slot")
    public String saveOrUpdate(@RequestBody ParkingSlot parkingSlot, @RequestParam String token) {
        return service.saveOrUpdate(parkingSlot);
    }
	
	@PostMapping(value = "/parking-slots-upload-excel", consumes = "multipart/form-data")
	public ResponseEntity<String> uploadParkingSlots(
	        @RequestParam("file") MultipartFile file,
	        @RequestParam String token) throws IOException {

	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body(("Please upload a valid Excel file."));
	    }

	    String responses = service.uploadParkingSlots(file);
	    return ResponseEntity.ok(responses);
	}

    @GetMapping("/list slots by LetsWorkCentre")
    public PaginatedResponseDto listByLetsWorkCentre(
            @RequestParam String letsWorkCentre,
            @RequestParam String companyId,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String token) {
        return service.listByLetsWorkCentre(letsWorkCentre, companyId, city, state, page);
    }

    @DeleteMapping("/delete parking slot")
    public String delete(@RequestBody ParkingSlot parkingSlot, @RequestParam String token) {
        return service.deleteParkingSlot(parkingSlot);
    }

}

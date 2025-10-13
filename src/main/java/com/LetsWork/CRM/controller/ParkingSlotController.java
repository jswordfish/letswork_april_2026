package com.LetsWork.CRM.controller;

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

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.ParkingSlot;
import com.LetsWork.CRM.service.ParkingSlotService;


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
	public ResponseEntity<List<String>> uploadParkingSlots(
	        @RequestParam("file") MultipartFile file,
	        @RequestParam("companyId") String companyId,
	        @RequestParam String token) throws IOException {

	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body(List.of("Please upload a valid Excel file."));
	    }

	    List<String> responses = service.uploadParkingSlots(file, companyId);
	    return ResponseEntity.ok(responses);
	}

    @GetMapping("/list slots by location")
    public PaginatedResponseDto listByLocation(
            @RequestParam String location,
            @RequestParam String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String token) {
        return service.listByLocation(location, companyId, page);
    }

    @DeleteMapping("/delete parking slot")
    public String delete(@RequestBody ParkingSlot parkingSlot, @RequestParam String token) {
        return service.deleteParkingSlot(parkingSlot);
    }

}

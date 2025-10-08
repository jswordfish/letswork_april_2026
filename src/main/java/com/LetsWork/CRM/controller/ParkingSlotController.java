package com.LetsWork.CRM.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

package com.LetsWork.CRM.controller;

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

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.ConferenceRoom;
import com.LetsWork.CRM.service.ConferenceRoomService;


@RestController
@CrossOrigin
public class ConferenceRoomController {
	
	@Autowired
	ConferenceRoomService service;
	
	@PostMapping("/create Conference Room")
	public String createOrUpdate(@RequestBody ConferenceRoom conferenceRoom, @RequestParam String token) {
		
		return service.saveOrUpdate(conferenceRoom);
		
	}
	
	@GetMapping("/Find by location")
	public List<ConferenceRoom> findByLocation(@RequestParam String location, @RequestParam String companyId, @RequestParam String token){
		return service.findByLocation(location, companyId);
	}
	
	@GetMapping("/find by location paginated")
    public ResponseEntity<PaginatedResponseDto> findByLocation(
            @RequestParam String location,
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.findByLocation(location, companyId, page));
    }

    
//    @GetMapping("/get available rooms paginated")
//    public ResponseEntity<PaginatedResponseDto> findAvailableConferenceRooms(
//            @RequestParam(defaultValue = "true") Boolean available,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam String token) {
//        return ResponseEntity.ok(service.findAvailableConferenceRooms(available, page));
//    }
	
	@DeleteMapping("/delete room")
	public String deleteRoom(@RequestBody ConferenceRoom conferenceRoom, @RequestParam String token) {
		
		return service.deleteByName(conferenceRoom);
		
	}
	

}

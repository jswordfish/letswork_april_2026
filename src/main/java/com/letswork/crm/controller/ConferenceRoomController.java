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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.service.ConferenceRoomService;


@RestController
@CrossOrigin
@RequestMapping("/conferenceRoom")
public class ConferenceRoomController {
	
	@Autowired
	ConferenceRoomService service;
	
	@PostMapping
	public String createOrUpdate(@RequestBody ConferenceRoom conferenceRoom, @RequestParam String token) {
		
		return service.saveOrUpdate(conferenceRoom);
		
	}
	
	
	@PostMapping(value = "/upload-excel", consumes = "multipart/form-data")
	public ResponseEntity<String> uploadConferenceRooms(
	        @RequestParam("file") MultipartFile file,
	        @RequestParam String token) throws IOException {

	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body(("Please upload a valid Excel file."));
	    }

	    String responses = service.uploadConferenceRooms(file);
	    return ResponseEntity.ok(responses);
	}
	
//	@GetMapping("/Find by LetsWorkCentre")
//	public List<ConferenceRoom> findByLetsWorkCentre(@RequestParam String letsWorkCentre, @RequestParam String companyId,
//			@RequestParam String city,
//			@RequestParam String state,
//			@RequestParam String token){
//		return service.findByLetsWorkCentre(letsWorkCentre, companyId, city, state);
//	}
	
//	@GetMapping("/find-by-LetsWorkCentre")
//    public ResponseEntity<PaginatedResponseDto> findByLetsWorkCentre(
//            @RequestParam String letsWorkCentre,
//            @RequestParam String city,
//			@RequestParam String state,
//            @RequestParam String companyId,
//            @RequestParam String token,
//            @RequestParam(defaultValue = "0") int page) {
//        return ResponseEntity.ok(service.findByLetsWorkCentre(letsWorkCentre, companyId, city, state, page));
//    }
	
	@GetMapping
	public ResponseEntity<PaginatedResponseDto> listConferenceRooms(
	        @RequestParam String companyId,
	        @RequestParam(required = false) String letsWorkCentre,
	        @RequestParam(required = false) String city,
	        @RequestParam(required = false) String state,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) String sort,   // example: name=asc
	        @RequestParam String token,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {

	    return ResponseEntity.ok(
	            service.listConferenceRooms(
	                    companyId,
	                    letsWorkCentre,
	                    city,
	                    state,
	                    search,
	                    sort,
	                    page,
	                    size
	            )
	    );
	}

    
//    @GetMapping("/get available rooms paginated")
//    public ResponseEntity<PaginatedResponseDto> findAvailableConferenceRooms(
//            @RequestParam(defaultValue = "true") Boolean available,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam String token) {
//        return ResponseEntity.ok(service.findAvailableConferenceRooms(available, page));
//    }
	
	@DeleteMapping
	public String deleteRoom(@RequestBody ConferenceRoom conferenceRoom, @RequestParam String token) {
		
		return service.deleteByName(conferenceRoom);
		
	}
	

}

package com.letswork.crm.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Cabin;
import com.letswork.crm.enums.CabinStatus;
import com.letswork.crm.service.CabinService;

@RestController
@RequestMapping("/cabin")
@CrossOrigin
public class CabinController {

    @Autowired
    private CabinService cabinService;

    @PostMapping
    public ResponseEntity<Cabin> saveOrUpdate(@RequestBody Cabin cabin, @RequestParam String token) {
    	
    	try {
    	Cabin res = cabinService.saveOrUpdate(cabin);
    	
        return ResponseEntity.ok(res);
    	}catch (DataIntegrityViolationException ex) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "This name for this cabin already exists."
	        );
	    }
    }
    
    @PatchMapping("/{cabinId}/status")
    public ResponseEntity<?> changeCabinStatus(
            @PathVariable Long cabinId,
            @RequestParam CabinStatus status,
            @RequestParam String token) {

        Cabin cabin = cabinService.changeCabinStatus(cabinId, status);

        return ResponseEntity.ok(cabin);
    }

//    @GetMapping
//    public PaginatedResponseDto listAll(@RequestParam String companyId,
//                                        @RequestParam(defaultValue = "0") int page,
//                                        @RequestParam(defaultValue = "10") int size,
//                                        @RequestParam String token) {
//        return cabinService.listAll(companyId, page, size);
//    }
    
    @GetMapping
    public ResponseEntity<PaginatedResponseDto> listCabins(
            @RequestParam String companyId,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) CabinStatus cabinStatus,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                cabinService.listCabins(
                        companyId,
                        letsWorkCentre,
                        city,
                        state,
                        search,
                        sort,
                        cabinStatus,
                        page,
                        size
                )
        );
    }

    
    @PostMapping(value = "/uploadCabins",
		    consumes = "multipart/form-data")
    public String uploadCabins(@RequestParam("file") MultipartFile file,
    		@RequestParam String token) throws IOException {
        return cabinService.uploadCabins(file);
    }
    
//    @GetMapping("/find-by-LetsWorkCentre")
//    public ResponseEntity<PaginatedResponseDto> findByLetsWorkCentre(
//            @RequestParam String letsWorkCentre,
//            @RequestParam String city,
//			@RequestParam String state,
//            @RequestParam String companyId,
//            @RequestParam String token,
//            @RequestParam(defaultValue = "0") int page) {
//        return ResponseEntity.ok(cabinService.findByLetsWorkCentre(letsWorkCentre, companyId, city, state, page));
//    }
}

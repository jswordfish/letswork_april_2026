package com.letswork.crm.controller;

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

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.UserSeatMapping;
import com.LetsWork.CRM.service.UserSeatMappingService;

@RestController
@CrossOrigin
@RequestMapping("/user-seat-mapping")
public class UserSeatMappingController {
	
	@Autowired
    private UserSeatMappingService userSeatMappingService;

    @PostMapping("/save-or-update")
    public ResponseEntity<UserSeatMapping> saveOrUpdate(@RequestBody UserSeatMapping mapping, @RequestParam String token) {
        return ResponseEntity.ok(userSeatMappingService.saveOrUpdate(mapping));
    }

    @GetMapping("/list")
    public ResponseEntity<PaginatedResponseDto> listMappings(
            @RequestParam String companyId,
            @RequestParam String location,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String token) {

        return ResponseEntity.ok(userSeatMappingService.listMappings(companyId, location, pageNo, pageSize));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMapping(@RequestParam Long id, @RequestParam String token) {
        userSeatMappingService.deleteMapping(id);
        return ResponseEntity.ok("User-seat mapping deleted successfully");
    }

}

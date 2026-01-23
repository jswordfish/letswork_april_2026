package com.letswork.crm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Greviance;
import com.letswork.crm.enums.GrevianceStatus;
import com.letswork.crm.service.GrevianceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/greviance")
@RequiredArgsConstructor
public class GrevianceController {

    private final GrevianceService grevianceService;

    @PostMapping("/save")
    public ResponseEntity<Greviance> save(
            @RequestParam String token,
            @RequestBody Greviance greviance
    ) {
        return ResponseEntity.ok(
                grevianceService.saveGreviance(greviance)
        );
    }

    @GetMapping("/get")
    public ResponseEntity<PaginatedResponseDto> get(
            @RequestParam String companyId,
            @RequestParam String token,

            @RequestParam(required = false) String email,
            @RequestParam(required = false) String centre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                grevianceService.getGreviances(
                        companyId,
                        email,
                        centre,
                        city,
                        state,
                        page,
                        size
                )
        );
    }
    
    @PutMapping("/update-status")
    public ResponseEntity<Greviance> updateStatus(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam Long grevianceId,
            @RequestParam GrevianceStatus status
    ) {

        return ResponseEntity.ok(
                grevianceService.updateGrevianceStatus(
                        grevianceId,
                        status,
                        companyId
                )
        );
    }
    
}

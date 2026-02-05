package com.letswork.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.BulkSeatAssignmentRequestContract;
import com.letswork.crm.entities.ContractSeatMapping;
import com.letswork.crm.service.ContractSeatMappingService;

@RestController
@RequestMapping("/contract-seats")
public class ContractSeatMappingController {

    @Autowired
    private ContractSeatMappingService service;

    @PostMapping("/assign")
    public ResponseEntity<List<ContractSeatMapping>> assignMultipleSeats(
            @RequestBody BulkSeatAssignmentRequestContract request,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.assignMultipleSeatsToContract(request));
    }

    @GetMapping
    public ResponseEntity<List<ContractSeatMapping>> getSeatsByContract(
            @RequestParam Long contractId,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.getSeatsByContract(contractId, companyId));
    }
}

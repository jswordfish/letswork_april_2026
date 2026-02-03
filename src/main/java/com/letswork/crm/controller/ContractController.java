package com.letswork.crm.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.enums.ContractStatus;
import com.letswork.crm.service.ContractService;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping
    public ResponseEntity<Contract> saveOrUpdate(
            @RequestBody Contract contract,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(contractService.saveOrUpdate(contract));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> getPaginated(
            @RequestParam String companyId,
            @RequestParam String token,

            @RequestParam(required = false) Long letsWorkClientId,
            @RequestParam(required = false) ContractStatus status,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                contractService.getPaginated(
                        companyId,
                        letsWorkClientId,
                        status,
                        page,
                        size
                )
        );
    }
}

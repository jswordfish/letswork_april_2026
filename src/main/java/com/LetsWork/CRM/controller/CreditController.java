package com.LetsWork.CRM.controller;

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
import com.LetsWork.CRM.entities.Credit;
import com.LetsWork.CRM.service.CreditService;

@RestController
@RequestMapping("/api/credits")
@CrossOrigin
public class CreditController {
	
	@Autowired
    private CreditService creditService;

    @PostMapping("/create credits")
    public ResponseEntity<Credit> saveOrUpdateCredit(@RequestBody Credit credit, @RequestParam String token) {
        
        Credit savedCredit = creditService.saveOrUpdate(credit);
        return ResponseEntity.ok(savedCredit);
    }

    @GetMapping("/get credits")
    public ResponseEntity<PaginatedResponseDto> listCredits(
        @RequestParam String companyId, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam String token) {
            
        PaginatedResponseDto response = creditService.listAll(companyId, page, size);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete credit by id")
    public ResponseEntity<String> deleteCredit(@RequestParam Long id, @RequestParam String token) {
        creditService.deleteById(id);
        return ResponseEntity.ok("Credit deleted successfully.");
    }

}

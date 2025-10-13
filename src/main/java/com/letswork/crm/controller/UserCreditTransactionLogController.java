package com.letswork.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.UserCreditTransactionLog;
import com.LetsWork.CRM.service.UserCreditTransactionLogService;
import com.LetsWork.CRM.util.InsufficientCreditsException;

@RestController
@CrossOrigin
public class UserCreditTransactionLogController {
	
	@Autowired
    private UserCreditTransactionLogService transactionService;

    // API to create a new transaction (debit or credit)
    @PostMapping("/create a new transaction")
    public ResponseEntity<?> logTransaction(@RequestBody UserCreditTransactionLog transactionLog, @RequestParam String token) {
        // transactionLog must contain companyId, userEmail, totalCredits, and creditTransactionType
        try {
            UserCreditTransactionLog savedLog = transactionService.logAndProcessTransaction(transactionLog);
            return ResponseEntity.ok(savedLog);
        } catch (InsufficientCreditsException e) {
            // Return 400 Bad Request with the specific failure message
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    // API to list transactions for a specific user
    @GetMapping("/list transactions for a specific user")
    public ResponseEntity<PaginatedResponseDto> listUserTransactions(
        @RequestParam String email,
        @RequestParam String companyId, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam String token) {
            
        PaginatedResponseDto response = transactionService.listUserTransactions(email, companyId, page, size);
        return ResponseEntity.ok(response);
    }

    // API to list all transactions for a company
    @GetMapping("/list all transactions for a company")
    public ResponseEntity<PaginatedResponseDto> listAllTransactions(
        @RequestParam String companyId, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam String token) {
            
        PaginatedResponseDto response = transactionService.listAllTransactions(companyId, page, size);
        return ResponseEntity.ok(response);
    }


}

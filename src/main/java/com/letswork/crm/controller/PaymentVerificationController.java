package com.letswork.crm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.VerifyPaymentRequest;
import com.letswork.crm.service.PaymentVerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentVerificationController {

    private final PaymentVerificationService paymentVerificationService;

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody VerifyPaymentRequest request, @RequestParam String token) {
        paymentVerificationService.verifyAndProcessPayment(request.getPaymentId());
        return new ResponseEntity<>("Payment verified and processed successfully", HttpStatus.OK);
    }
}

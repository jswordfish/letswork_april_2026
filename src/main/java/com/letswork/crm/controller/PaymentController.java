package com.letswork.crm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/link")
    public ResponseEntity<String> createPaymentLink(
            @RequestParam Long invoiceId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                paymentService.createPaymentLink(invoiceId).toString()
        );
    }
}

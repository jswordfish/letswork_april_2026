package com.letswork.crm.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.PaymentVerificationResponseDto;
import com.letswork.crm.dtos.VerifyPaymentRequest;
import com.letswork.crm.service.PaymentVerificationService;
import com.razorpay.Utils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentVerificationController {

    private final PaymentVerificationService paymentVerificationService;

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponseDto> verifyPayment(
            @RequestBody VerifyPaymentRequest request,
            @RequestParam String token
    ) {
    	PaymentVerificationResponseDto response = paymentVerificationService
                .verifyAndProcessPayment(request.getPaymentId(), request.getReferenceId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
//    @PostMapping("/webhook")
//    public ResponseEntity<PaymentVerificationResponseDto> handleWebhook(
//            @RequestHeader("X-Razorpay-Signature") String signature,
//            @RequestBody String payload
//    ) {
//        try {
//            // ✅ 1. Verify signature (IMPORTANT)
//            boolean isValid = verifySignature(payload, signature);
//
//            if (!isValid) {
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }
//
//            JSONObject json = new JSONObject(payload);
//
//            // ✅ 2. Extract payment entity
//            JSONObject paymentEntity = json
//                    .getJSONObject("payload")
//                    .getJSONObject("payment")
//                    .getJSONObject("entity");
//
//            String paymentId = paymentEntity.getString("id");
//
//            // ✅ 3. Extract referenceId from notes
//            JSONObject notes = paymentEntity.getJSONObject("notes");
//            String referenceId = notes.getString("referenceId");
//
//            // ✅ 4. Call same service
//            PaymentVerificationResponseDto response = paymentVerificationService
//                    .verifyAndProcessPayment(paymentId, referenceId);
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Webhook processing failed", e);
//        }
//    }
//
//    // Signature verification
//    private boolean verifySignature(String payload, String signature) {
//        try {
//            String expectedSignature = Utils.getHash(payload, "thisismywebhooksecret");
//            return expectedSignature.equals(signature);
//        } catch (Exception e) {
//            return false;
//        }
//    }
}


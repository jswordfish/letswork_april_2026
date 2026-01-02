package com.letswork.crm.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.DayPassScanResponse;
import com.letswork.crm.entities.BookDayPass;
import com.letswork.crm.service.BookDayPassService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/book-day-pass")
@RequiredArgsConstructor
public class BookDayPassController {

    private final BookDayPassService service;

    @PostMapping
    public ResponseEntity<BookDayPass> book(
            @RequestParam String token,
            @RequestBody BookDayPass request
    ) {
        return ResponseEntity.ok(service.book(request));
    }
    
    @PostMapping("/scan")
    public ResponseEntity<DayPassScanResponse> scanDayPass(
            @RequestParam String qrData,
            @RequestParam String token
    ) {
        // Example qrData: DAYPASS|abc-123
        String bookingCode = qrData.split("\\|")[1];

        BookDayPass booking = service.scanAndConsume(
                bookingCode
        );

        return ResponseEntity.ok(
                DayPassScanResponse.from(booking)
        );
    }

    @GetMapping
    public ResponseEntity<List<BookDayPass>> get(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return ResponseEntity.ok(
                service.get(companyId, email, letsWorkCentre, city, state, date)
        );
    }
}

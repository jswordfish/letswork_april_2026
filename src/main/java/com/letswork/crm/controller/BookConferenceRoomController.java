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

import com.letswork.crm.dtos.ConferenceRoomScanResponse;
import com.letswork.crm.entities.BookConferenceRoom;
import com.letswork.crm.service.BookConferenceRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/book-conference-room")
@RequiredArgsConstructor
public class BookConferenceRoomController {

    private final BookConferenceRoomService service;

    @PostMapping
    public ResponseEntity<BookConferenceRoom> book(
            @RequestParam String token,
            @RequestBody BookConferenceRoom request
    ) {
        return ResponseEntity.ok(service.book(request));
    }

    @PostMapping("/scan")
    public ResponseEntity<ConferenceRoomScanResponse> scan(
            @RequestParam String qrData,
            @RequestParam String token
    ) {
        // Example: CONFROOM|uuid
        String bookingCode = qrData.split("\\|")[1];

        BookConferenceRoom booking =
                service.scanAndConsume(bookingCode);

        return ResponseEntity.ok(
                ConferenceRoomScanResponse.from(booking)
        );
    }

    @GetMapping
    public ResponseEntity<List<BookConferenceRoom>> get(
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
                service.get(
                        companyId,
                        email,
                        letsWorkCentre,
                        city,
                        state,
                        date
                )
        );
    }
}

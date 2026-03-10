package com.letswork.crm.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.BookConferenceRoomRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.AllBookings;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.BookingType;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.AllBookingsService;
import com.letswork.crm.serviceImpl.MailJetOtpService;

@RestController
@RequestMapping("/api/allBookings")
public class AllBookingsController {

    @Autowired
    private AllBookingsService service;
    
    @Autowired
    private MailJetOtpService mailService;

    @Autowired
    private LetsWorkClientRepository letsWorkClientRepository;

    @PostMapping("/day-pass")
    public ResponseEntity<AllBookings> createDayPassBooking(
            @RequestBody AllBookings request,
            @RequestParam String token) {

        return ResponseEntity.ok(service.createDayPassBooking(request));
    }
    
    @PostMapping("/conference-room")
    public ResponseEntity<AllBookings> bookConferenceRoom(
            @RequestParam String token,
            @RequestBody BookConferenceRoomRequest request
    ) {

        AllBookings bookingRequest = request.getBooking();

        AllBookings booking = service.createConferenceRoomBooking(
                bookingRequest,
                request.getSlotDate(),
                request.getSlots()
        );

        LetsWorkClient client =
                letsWorkClientRepository
                        .findByEmailAndCompanyId(
                                booking.getEmail(),
                                booking.getCompanyId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException("This company does not exist"));

        LocalTime startTime =
                request.getSlots()
                        .stream()
                        .map(ConferenceRoomSlotRequest::getStartTime)
                        .min(LocalTime::compareTo)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid slot data"));

        LocalTime endTime =
                request.getSlots()
                        .stream()
                        .map(ConferenceRoomSlotRequest::getEndTime)
                        .max(LocalTime::compareTo)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid slot data"));

        mailService.sendConferenceEmail(
                booking.getEmail(),
                request.getSlotDate(),
                booking.getId(),
                booking.getLetsWorkCentre(),
                booking.getQrS3Path(),
                client.getClientCompanyName(),
                startTime.toString(),
                endTime.toString(),
                booking.getRoomName()
        );

        return ResponseEntity.ok(booking);
    }
    
    @GetMapping
    public ResponseEntity<PaginatedResponseDto> getBookings(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) BookingType bookingType,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                service.getBookings(
                        companyId,
                        email,
                        letsWorkCentre,
                        city,
                        state,
                        bookingType,
                        status,
                        fromDate,
                        toDate,
                        page,
                        size
                )
        );
    }

}

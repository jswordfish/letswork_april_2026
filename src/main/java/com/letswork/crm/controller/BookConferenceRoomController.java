package com.letswork.crm.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.BookConferenceRoomRequest;
import com.letswork.crm.dtos.ConferenceRoomSlotRequest;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BookConferenceRoom;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.repo.BookConferenceRoomRepository;
import com.letswork.crm.repo.ConferenceRoomTimeSlotRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.BookConferenceRoomService;
import com.letswork.crm.serviceImpl.MailJetOtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/book-conference-room")
@RequiredArgsConstructor
public class BookConferenceRoomController {
	
	@Autowired
	ConferenceRoomTimeSlotRepository timeSlotRepo;
	
	@Autowired
	MailJetOtpService mailService;
	
	@Autowired
	LetsWorkClientRepository letsWorkClientRepository;
	
	@Autowired
	BookConferenceRoomRepository repo;

    private final BookConferenceRoomService service;

    @PostMapping
    public ResponseEntity<BookConferenceRoom> book(
            @RequestParam String token,
            @RequestBody BookConferenceRoomRequest request
    ) {

        BookConferenceRoom booking =
                service.book(
                        request.getBooking(),
                        request.getSlotDate(),
                        request.getSlots()
                );

        LetsWorkClient client = letsWorkClientRepository.findByEmailAndCompanyId(booking.getEmail(), booking.getCompanyId()).orElseThrow(() -> new RuntimeException("This company does not exists"));

        LocalTime startTime =
                request.getSlots()
                       .stream()
                       .map(ConferenceRoomSlotRequest::getStartTime)
                       .min(LocalTime::compareTo)
                       .orElseThrow(() ->
                               new RuntimeException("Invalid slot data")
                       );

        LocalTime endTime =
                request.getSlots()
                       .stream()
                       .map(ConferenceRoomSlotRequest::getEndTime)
                       .max(LocalTime::compareTo)
                       .orElseThrow(() ->
                               new RuntimeException("Invalid slot data")
                       );

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

    @GetMapping("/scan")
    public ResponseEntity<BookConferenceRoom> scan(
            @RequestParam String qrData,
            @RequestParam String token
    ) {
        // Example: CONFROOM|uuid
        String bookingCode = qrData.split("\\|")[1];

        BookConferenceRoom booking = repo.findByBookingCode(bookingCode).orElseThrow(() ->
        new RuntimeException("Booking not found")
);
                

        return ResponseEntity.ok(booking);
        
    }
    
    @PostMapping("/allow")
    public ResponseEntity<BookConferenceRoom> allow(
            @RequestBody BookConferenceRoom request,
            @RequestParam String token
    ) {

        if (Boolean.TRUE.equals(request.getUsed())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Conference room booking already used"
            );
        }

        LocalTime startTime = request.getSlots()
                .stream()
                .map(ConferenceRoomTimeSlot::getStartTime)
                .min(LocalTime::compareTo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No time slot found for booking"
                ));

        LocalTime endTime = request.getSlots()
                .stream()
                .map(ConferenceRoomTimeSlot::getEndTime)
                .max(LocalTime::compareTo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No time slot found for booking"
                ));

        LocalTime now = LocalTime.now();

        if (now.isBefore(startTime.minusHours(1))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Entry allowed only 1 hour before meeting start"
            );
        }

        if (now.isAfter(endTime)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Meeting has already ended"
            );
        }

        request.setUsed(true);
        repo.save(request);

        return ResponseEntity.ok(request);
    }
    
    @PutMapping("/cancel/{id}")
    public ResponseEntity<BookConferenceRoom> cancel(
            @PathVariable Long id,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.cancel(id, companyId));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> get(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false) String roomName,
            @RequestParam(required = false) BookingStatus currentStatus,   

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                service.getPaginated(
                        companyId,
                        email,
                        letsWorkCentre,
                        city,
                        state,
                        fromDate,
                        toDate,
                        roomName,
                        currentStatus,   
                        page,
                        size
                )
        );
    }
    
    @GetMapping("/availability")
    public ResponseEntity<List<ConferenceRoomTimeSlot>> getBookedSlots(
            @RequestParam String companyId,
            @RequestParam String letsWorkCentre,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String roomName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                timeSlotRepo.findBookedSlots(
                        companyId,
                        letsWorkCentre,
                        city,
                        state,
                        roomName,
                        date
                )
        );
    }
}

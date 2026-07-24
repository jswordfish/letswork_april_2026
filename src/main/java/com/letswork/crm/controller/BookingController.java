package com.letswork.crm.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Booking;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByBooking;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.repo.BookingRepository;
import com.letswork.crm.service.BookingService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/allBookings")
public class BookingController {
	
	@Autowired
	BookingService bookingService;
	
	@Autowired
	BookingRepository bookingRepo;
	
	@DeleteMapping("/draft/{bookingId}")
    public ResponseEntity<Map<String, Object>> deleteDraftBooking(
            @PathVariable Long bookingId,
            @RequestParam String token
    ) {

        bookingService.deleteDraftBooking(bookingId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Draft booking deleted successfully");

        return ResponseEntity.ok(response);
    }
	
	@PatchMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateBooking(@PathVariable("id") Long id, @RequestParam String token) {
        bookingService.deactivateBooking(id);
        
        return ResponseEntity.ok("Booking deactivated successfully.");
    }
	
	
	
	@GetMapping("/all")
	public ResponseEntity<PaginatedResponseDto> getAllBookings(
	        @RequestParam String companyId,
	        @RequestParam String token,
	        @RequestParam(required = false) List<String> bookingType,
	        @RequestParam(required = false) Long clientId,
	        @RequestParam(required = false) String referenceId,
	        @Parameter(array = @ArraySchema(schema = @Schema(type = "string")))
	        @RequestParam(required = false) List<BookingStatus> status,
	        @RequestParam(required = false) BookedFrom bookedFrom,
	        @RequestParam(required = false) List<String> roomNames, // Changed to List
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) List<String> letsWorkCentres, // Changed to List

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate fromDate,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate toDate,
	        
	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate startDateFromDate,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate startDateToDate,

	        @RequestParam(defaultValue = "DATE_OF_PURCHASE") SortFieldByBooking sortFieldByBooking,
	        @RequestParam(defaultValue = "DESC") SortingOrder order,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {
	    return ResponseEntity.ok(
	            bookingService.getAllBookings(
	                    companyId,
	                    bookingType,
	                    clientId,
	                    referenceId,
	                    status,
	                    bookedFrom,
	                    roomNames, // Passed as List
	                    search,
	                    letsWorkCentres, // Passed as List
	                    fromDate,
	                    toDate,
	                    startDateFromDate,
	                    startDateToDate,
	                    sortFieldByBooking,
	                    order,
	                    page,
	                    size
	            )
	    );
	}
	
	@GetMapping("/scan")
    public ResponseEntity<Booking> scan(
            @RequestParam String qrData,
            @RequestParam String token
    ) {
//        // Example: CONFROOM|uuid
//        String bookingCode = qrData.split("\\|")[1];

        Booking booking = bookingRepo.findByReferenceId(qrData).orElseThrow(() ->
        new RuntimeException("Booking not found")
);

        return ResponseEntity.ok(booking);
        
    }
	
	@PostMapping("/allow")
    public ResponseEntity<Booking> allow(
            @RequestParam Long bookingId,
            @RequestParam String token
    ) {
		
		Booking request = bookingRepo.findById(bookingId).orElse(null);
		
		if(request==null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking not found");
		}

        if (Boolean.TRUE.equals(request.getUsed())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Booking already used"
            );
        }

        LocalDate today = LocalDate.now();

        if (!today.equals(request.getStartDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking can only be used on the booking date"
            );
        }

        request.setUsed(true);
        bookingRepo.save(request);

        return ResponseEntity.ok(request);
    }
	
	@GetMapping("/all/export")
	public void exportAllBookings(
	        @RequestParam String companyId,
	        @RequestParam String token,
	        @RequestParam(required = false) List<String> bookingType,
	        @RequestParam(required = false) Long clientId,
	        @RequestParam(required = false) String referenceId,
	        @Parameter(array = @ArraySchema(schema = @Schema(type = "string")))
	        @RequestParam(required = false) List<BookingStatus> status,
	        @RequestParam(required = false) BookedFrom bookedFrom,
	        @RequestParam(required = false) List<String> roomNames,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) List<String> letsWorkCentres,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate fromDate,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate toDate,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate startDateFromDate,

	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate startDateToDate,

	        @RequestParam(defaultValue = "DATE_OF_PURCHASE") SortFieldByBooking sortFieldByBooking,
	        @RequestParam(defaultValue = "DESC") SortingOrder order,
	        HttpServletResponse response
	) throws IOException {

	    bookingService.exportAllBookings(
	            companyId,
	            bookingType,
	            clientId,
	            referenceId,
	            status,
	            bookedFrom,
	            roomNames,
	            search,
	            letsWorkCentres,
	            fromDate,
	            toDate,
	            startDateFromDate,
	            startDateToDate,
	            sortFieldByBooking,
	            order,
	            response
	    );
	}
	

}
package com.letswork.crm.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.enums.BookedFrom;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.SortFieldByBooking;
import com.letswork.crm.enums.SortingOrder;
import com.letswork.crm.service.BookingService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/allBookings")
public class BookingController {
	
	@Autowired
	BookingService bookingService;
	
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
	        @RequestParam(required = false) String roomName,
	        @RequestParam(required = false) String search,

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
	                    roomName,
	                    search,
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

}
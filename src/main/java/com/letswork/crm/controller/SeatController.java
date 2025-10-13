package com.letswork.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.Seat;
import com.LetsWork.CRM.enums.SeatType;
import com.LetsWork.CRM.service.SeatService;

@RestController
@CrossOrigin
@RequestMapping("/seat")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @PostMapping("/save-or-update")
    public ResponseEntity<Seat> saveOrUpdate(@RequestBody Seat seat, @RequestParam String token) {
        return ResponseEntity.ok(seatService.saveOrUpdate(seat));
    }

    @GetMapping("/list")
    public ResponseEntity<PaginatedResponseDto> listSeats(
            @RequestParam String companyId,
            @RequestParam String location,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String token) {

        return ResponseEntity.ok(seatService.listSeats(companyId, location, pageNo, pageSize));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteSeat(@RequestParam Long id, @RequestParam String token) {
        seatService.deleteSeat(id);
        return ResponseEntity.ok("Seat deleted successfully");
    }
    
    @GetMapping("/total seats")
    public ResponseEntity<Long> getTotalSeats(@RequestParam String companyId,
                                              @RequestParam String location,
                                              @RequestParam SeatType seatType,
                                              @RequestParam String token) {
        long totalSeats = seatService.getTotalSeats(companyId, location, seatType);
        return ResponseEntity.ok(totalSeats);
    }

    @GetMapping("/available seats")
    public ResponseEntity<Long> getAvailableSeats(@RequestParam String companyId,
                                                  @RequestParam String location,
                                                  @RequestParam SeatType seatType,
                                                  @RequestParam String token) {
        long availableSeats = seatService.getAvailableSeats(companyId, location, seatType);
        return ResponseEntity.ok(availableSeats);
    }
    
}

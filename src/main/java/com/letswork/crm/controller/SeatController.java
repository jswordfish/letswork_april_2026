package com.letswork.crm.controller;

import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.enums.SeatType;
import com.letswork.crm.service.SeatService;

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
    
    //upload excel of seat
    @PostMapping("/upload-excel")
    public ResponseEntity<List<String>> uploadSeatExcel(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(seatService.uploadSeatExcel(file));
    }

    @GetMapping("/list")
    public ResponseEntity<PaginatedResponseDto> listSeats(
            @RequestParam String companyId,
            @RequestParam String letsWorkCentre,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String token) {

        return ResponseEntity.ok(seatService.listSeats(companyId, letsWorkCentre, pageNo, pageSize));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteSeat(@RequestParam Long id, @RequestParam String token) {
        seatService.deleteSeat(id);
        return ResponseEntity.ok("Seat deleted successfully");
    }
    
    @GetMapping("/total seats")
    public ResponseEntity<Long> getTotalSeats(@RequestParam String companyId,
                                              @RequestParam String letsWorkCentre,
                                              @RequestParam SeatType seatType,
                                              @RequestParam String token) {
        long totalSeats = seatService.getTotalSeats(companyId, letsWorkCentre, seatType);
        return ResponseEntity.ok(totalSeats);
    }

    @GetMapping("/available seats")
    public ResponseEntity<Long> getAvailableSeats(@RequestParam String companyId,
                                                  @RequestParam String letsWorkCentre,
                                                  @RequestParam SeatType seatType,
                                                  @RequestParam String token) {
        long availableSeats = seatService.getAvailableSeats(companyId, letsWorkCentre, seatType);
        return ResponseEntity.ok(availableSeats);
    }
    
}

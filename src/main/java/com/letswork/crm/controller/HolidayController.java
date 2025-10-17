package com.letswork.crm.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.service.HolidayService;

@RestController
@CrossOrigin
@RequestMapping("/api/holidays")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @PostMapping(value = "/uploadHolidays",
		    consumes = "multipart/form-data")
    public String uploadHolidays(@RequestParam("file") MultipartFile file, @RequestParam String token) throws IOException {
        return holidayService.uploadHolidays(file);
    }

    @GetMapping("/list")
    public ResponseEntity<PaginatedResponseDto> listHolidays(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String token) {

        return ResponseEntity.ok(
                holidayService.listHolidays(companyId, letsWorkCentre, city, state, page, size)
        );
    }
}

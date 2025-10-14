package com.letswork.crm.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Printer;
import com.letswork.crm.service.PrinterService;

@RestController
@CrossOrigin
public class PrinterController {
	
	@Autowired
	private PrinterService service;

    @PostMapping
    public ResponseEntity<Printer> createOrUpdatePrinter(@RequestBody Printer printer, @RequestParam String token) {
        return ResponseEntity.ok(service.saveOrUpdate(printer));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> listPrinters(
            @RequestParam String companyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String token) {
        return ResponseEntity.ok(service.listPrinters(companyId, page, size));
    }

    @DeleteMapping("/delete printer by id")
    public ResponseEntity<String> deletePrinter(@RequestParam Long id, @RequestParam String token) {
        service.deletePrinter(id);
        return ResponseEntity.ok("Printer deleted successfully.");
    }

    @PostMapping("/upload-excel")
    public ResponseEntity<List<String>> uploadPrintersExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam String token) throws IOException {
        return ResponseEntity.ok(service.uploadPrintersFromExcel(file));
    }

}

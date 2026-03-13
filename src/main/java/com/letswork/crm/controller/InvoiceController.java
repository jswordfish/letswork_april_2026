package com.letswork.crm.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.enums.BookingType;
import com.letswork.crm.enums.InvoiceStatus;
import com.letswork.crm.service.InvoiceService;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<Invoice> saveInvoice(
            @RequestBody Invoice invoice,
            @RequestParam String token
    ) {

        Invoice saved =
                invoiceService.saveInvoice(invoice);

        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> getInvoices(
            @RequestParam String companyId,
            @RequestParam String token,

            @RequestParam(required = false) String email,
            @RequestParam(required = false) InvoiceStatus invoiceStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PaginatedResponseDto response =
                invoiceService.getPaginated(
                        companyId,
                        email,
                        invoiceStatus,
                        fromDate,
                        toDate,
                        page,
                        size
                );

        return ResponseEntity.ok(response);
    }
}

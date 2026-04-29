package com.letswork.crm.service;

import java.time.LocalDateTime;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.enums.InvoiceStatus;

public interface InvoiceService {

    Invoice saveInvoice(Invoice invoice);

    PaginatedResponseDto getPaginated(
            String companyId,
            String email,
            InvoiceStatus invoiceStatus,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size
    );
}

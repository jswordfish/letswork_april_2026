package com.letswork.crm.service;

import java.util.Date;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.enums.BookingType;
import com.letswork.crm.enums.InvoiceStatus;

public interface InvoiceService {

    Invoice saveInvoice(Invoice invoice);

    PaginatedResponseDto getPaginated(
            String companyId,
            String email,
            InvoiceStatus invoiceStatus,
            Date fromDate,
            Date toDate,
            int page,
            int size
    );
}

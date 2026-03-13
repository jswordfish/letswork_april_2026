package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Invoice;
import com.letswork.crm.enums.BookingType;
import com.letswork.crm.enums.InvoiceStatus;
import com.letswork.crm.repo.InvoiceRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.InvoiceService;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private LetsWorkClientRepository clientRepository;

    @Override
    public Invoice saveInvoice(Invoice invoice) {

        // ✅ Email validation
        clientRepository.findByEmailAndCompanyId(
                invoice.getCompanyEmail(),
                invoice.getCompanyId()
        ).orElseThrow(() ->
                new RuntimeException("Client email not found"));

        // ✅ populate base fields
        invoice.setCreateDate(new Date());

        return invoiceRepository.save(invoice);
    }

    @Override
    public PaginatedResponseDto getPaginated(
            String companyId,
            String email,
            InvoiceStatus invoiceStatus,
            Date fromDate,
            Date toDate,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createDate").descending()
        );

        Page<Invoice> resultPage = invoiceRepository.filter(
                companyId,
                email,
                invoiceStatus,
                fromDate,
                toDate,
                pageable
        );

        PaginatedResponseDto dto = new PaginatedResponseDto();

        dto.setSelectedPage(page);
        dto.setTotalNumberOfRecords((int) resultPage.getTotalElements());
        dto.setTotalNumberOfPages(resultPage.getTotalPages());
        dto.setRecordsFrom(page * size + 1);
        dto.setRecordsTo(
                Math.min((page + 1) * size,
                        (int) resultPage.getTotalElements())
        );
        dto.setList(resultPage.getContent());

        return dto;
    }
}

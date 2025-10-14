package com.letswork.crm.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Printer;

@Repository
public interface PrinterRepository extends JpaRepository<Printer, Long> {

    Printer findByPrinterNameAndLocationAndCompanyId(String printerName, String location, String companyId);

    Page<Printer> findAllByCompanyId(String companyId, Pageable pageable);
}
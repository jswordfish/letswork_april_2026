package com.letswork.crm.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Invoice;
import com.letswork.crm.enums.BookingType;
import com.letswork.crm.enums.InvoiceStatus;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

	@Query("SELECT i FROM Invoice i " +
	           "WHERE i.companyId = :companyId " +
	           "AND (:email IS NULL OR i.companyEmail = :email) " +
	           "AND (:bookingType IS NULL OR i.bookingType = :bookingType) " +
	           "AND (:invoiceStatus IS NULL OR i.invoiceStatus = :invoiceStatus) " +
	           "AND (:fromDate IS NULL OR DATE(i.createDate) >= :fromDate) " +
	           "AND (:toDate IS NULL OR DATE(i.createDate) <= :toDate)")
	    Page<Invoice> filter(
	            @Param("companyId") String companyId,
	            @Param("email") String email,
	            @Param("bookingType") BookingType bookingType,
	            @Param("invoiceStatus") InvoiceStatus invoiceStatus,
	            @Param("fromDate") java.time.LocalDate fromDate,
	            @Param("toDate") java.time.LocalDate toDate,
	            Pageable pageable
	    );
}

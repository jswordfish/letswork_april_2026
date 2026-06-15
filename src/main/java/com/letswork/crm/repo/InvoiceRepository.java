package com.letswork.crm.repo;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Invoice;
import com.letswork.crm.enums.InvoiceStatus;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

	@Query(
		    "SELECT i " +
		    "FROM Invoice i " +
		    "WHERE i.companyId = :companyId " +

		    "AND (:email IS NULL " +
		    "     OR LOWER(i.booking.letsWorkClient.email) = LOWER(:email)) " +

		    "AND (:invoiceStatus IS NULL " +
		    "     OR i.invoiceStatus = :invoiceStatus) " +

		    "AND (:fromDate IS NULL " +
		    "     OR i.dateOfCreation >= :fromDate) " +

		    "AND (:toDate IS NULL " +
		    "     OR i.dateOfCreation <= :toDate) " +

		    "AND ( " +
		    "    :search IS NULL " +
		    "    OR :search = '' " +

		    "    OR LOWER(i.booking.letsWorkClient.email) " +
		    "            LIKE LOWER(CONCAT('%', :search, '%')) " +

		    "    OR LOWER(i.booking.letsWorkClient.clientCompanyName) " +
		    "            LIKE LOWER(CONCAT('%', :search, '%')) " +


		    "    OR LOWER(i.booking.referenceId) " +
		    "            LIKE LOWER(CONCAT('%', :search, '%')) " +

		    "    OR LOWER(i.booking.razorpayOrderId) " +
		    "            LIKE LOWER(CONCAT('%', :search, '%')) " +

		    "    OR LOWER(i.booking.bookingType) " +
		    "            LIKE LOWER(CONCAT('%', :search, '%')) " +

		    "    OR LOWER(CAST(i.invoiceStatus AS string)) " +
		    "            LIKE LOWER(CONCAT('%', :search, '%')) " +

		    "    OR LOWER(CAST(i.booking.bookingStatus AS string)) " +
		    "            LIKE LOWER(CONCAT('%', :search, '%')) " +

		    "    OR LOWER(i.pdfS3KeyName) " +
		    "            LIKE LOWER(CONCAT('%', :search, '%')) " +

		    "    OR CAST(i.id AS string) " +
		    "            LIKE CONCAT('%', :search, '%') " +

		    "    OR CAST(i.amountFinal AS string) " +
		    "            LIKE CONCAT('%', :search, '%') " +

		    "    OR CAST(i.amount AS string) " +
		    "            LIKE CONCAT('%', :search, '%') " +
		    ")"
		)
		Page<Invoice> filter(
		        @Param("companyId") String companyId,
		        @Param("email") String email,
		        @Param("invoiceStatus") InvoiceStatus invoiceStatus,
		        @Param("search") String search,
		        @Param("fromDate") LocalDate fromDate,
		        @Param("toDate") LocalDate toDate,
		        Pageable pageable
		);
	
	@Query("SELECT i FROM Invoice i JOIN FETCH i.booking WHERE i.booking.referenceId = :referenceId")
	Optional<Invoice> findByBookingReferenceId(@Param("referenceId") String referenceId);
	
}

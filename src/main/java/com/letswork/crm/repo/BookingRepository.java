package com.letswork.crm.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Booking;
import com.letswork.crm.enums.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	
	
	Optional<Booking> findByReferenceId(String referenceId);
	
	@Query(
	        value = "SELECT * FROM booking b " +
	                "WHERE b.company_id = :companyId " +
	                "AND b.booking_status <> 'DRAFT' " +

	                "AND (:clientId IS NULL OR b.lets_work_client_id = :clientId) " +
	                "AND (:referenceId IS NULL OR b.reference_id = :referenceId) " +

	                // ✅ FIXED HERE (CSV check instead of IN)
	                "AND (:statusCsv IS NULL OR FIND_IN_SET(b.booking_status, :statusCsv)) " +

	                "AND (:bookedFrom IS NULL OR b.booked_from = :bookedFrom) " +

	                "AND (:fromDate IS NULL OR b.date_of_purchase >= :fromDate) " +
	                "AND (:toDate IS NULL OR b.date_of_purchase <= :toDate) " +
	                
					"AND (:startDateFromDate IS NULL OR b.start_date >= :startDateFromDate) " +
					"AND (:startDateToDate IS NULL OR b.start_date <= :startDateToDate) " +

	                "AND ( " +
	                "   :roomName IS NULL OR EXISTS ( " +
	                "       SELECT 1 FROM conference_room cr " +
	                "       WHERE cr.id = b.conference_room_id " +
	                "       AND LOWER(cr.name) = LOWER(:roomName) " +
	                "   ) " +
	                ") " +

	                "AND ( " +
	                "   :search IS NULL OR " +
	                "   CAST(b.id AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   LOWER(b.reference_id) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
	                "   LOWER(b.razorpay_order_id) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
	                "   CAST(b.amount AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   CAST(b.booking_type AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   EXISTS ( " +
	                "       SELECT 1 FROM conference_room cr " +
	                "       WHERE cr.id = b.conference_room_id " +
	                "       AND LOWER(cr.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
	                "   ) " +
	                ")",
	        countQuery = "SELECT COUNT(*) FROM booking b " +
	                "WHERE b.company_id = :companyId " +
	                "AND b.booking_status <> 'DRAFT' " +
	                "AND (:clientId IS NULL OR b.lets_work_client_id = :clientId) " +
	                "AND (:referenceId IS NULL OR b.reference_id = :referenceId) " +
	                "AND (:statusCsv IS NULL OR FIND_IN_SET(b.booking_status, :statusCsv)) " +
	                "AND (:bookedFrom IS NULL OR b.booked_from = :bookedFrom) " +
	                "AND (:fromDate IS NULL OR b.date_of_purchase >= :fromDate) " +
	                "AND (:toDate IS NULL OR b.date_of_purchase <= :toDate) " +
	                "AND (:startDateFromDate IS NULL OR b.start_date >= :startDateFromDate) " +
	                "AND (:startDateToDate IS NULL OR b.start_date <= :startDateToDate) " +
	                "AND ( " +
	                "   :roomName IS NULL OR EXISTS ( " +
	                "       SELECT 1 FROM conference_room cr " +
	                "       WHERE cr.id = b.conference_room_id " +
	                "       AND LOWER(cr.name) = LOWER(:roomName) " +
	                "   ) " +
	                ") " +
	                "AND ( " +
	                "   :search IS NULL OR " +
	                "   CAST(b.id AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   LOWER(b.reference_id) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
	                "   LOWER(b.razorpay_order_id) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
	                "   CAST(b.amount AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   CAST(b.booking_type AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   EXISTS ( " +
	                "       SELECT 1 FROM conference_room cr " +
	                "       WHERE cr.id = b.conference_room_id " +
	                "       AND LOWER(cr.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
	                "   ) " +
	                ")",
	        nativeQuery = true
	)
	Page<Booking> filterAllBookings(
	        @Param("companyId") String companyId,
	        @Param("clientId") Long clientId,
	        @Param("referenceId") String referenceId,
	        @Param("statusCsv") String statusCsv,
	        @Param("bookedFrom") String bookedFrom,
	        @Param("roomName") String roomName,
	        @Param("search") String search,
	        @Param("fromDate") LocalDateTime fromDate,
	        @Param("toDate") LocalDateTime toDate,
	        @Param("startDateFromDate") LocalDate startDateFromDate,
	        @Param("startDateToDate") LocalDate startDateToDate,
	        Pageable pageable
	);

	@Query(
	        value = "SELECT * FROM booking b " +
	                "WHERE b.company_id = :companyId " +
	                "AND b.booking_status <> 'DRAFT' " +
	                "AND b.booking_type IN (:bookingTypes) " +

	                "AND (:clientId IS NULL OR b.lets_work_client_id = :clientId) " +
	                "AND (:referenceId IS NULL OR b.reference_id = :referenceId) " +

	                // ✅ SAME FIX HERE
	                "AND (:statusCsv IS NULL OR FIND_IN_SET(b.booking_status, :statusCsv)) " +

	                "AND (:bookedFrom IS NULL OR b.booked_from = :bookedFrom) " +

	                "AND (:fromDate IS NULL OR b.date_of_purchase >= :fromDate) " +
	                "AND (:toDate IS NULL OR b.date_of_purchase <= :toDate) " +
	                
					"AND (:startDateFromDate IS NULL OR b.start_date >= :startDateFromDate) " +
					"AND (:startDateToDate IS NULL OR b.start_date <= :startDateToDate) " +

	                "AND ( " +
	                "   :roomName IS NULL OR EXISTS ( " +
	                "       SELECT 1 FROM conference_room cr " +
	                "       WHERE cr.id = b.conference_room_id " +
	                "       AND LOWER(cr.name) = LOWER(:roomName) " +
	                "   ) " +
	                ") " +

	                "AND ( " +
	                "   :search IS NULL OR " +
	                "   CAST(b.id AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   LOWER(b.reference_id) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
	                "   LOWER(b.razorpay_order_id) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
	                "   CAST(b.amount AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   CAST(b.booking_type AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   EXISTS ( " +
	                "       SELECT 1 FROM conference_room cr " +
	                "       WHERE cr.id = b.conference_room_id " +
	                "       AND LOWER(cr.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
	                "   ) " +
	                ")",
	        countQuery = "SELECT COUNT(*) FROM booking b " +
	                "WHERE b.company_id = :companyId " +
	                "AND b.booking_status <> 'DRAFT' " +
	                "AND b.booking_type IN (:bookingTypes) " +
	                "AND (:clientId IS NULL OR b.lets_work_client_id = :clientId) " +
	                "AND (:referenceId IS NULL OR b.reference_id = :referenceId) " +
	                "AND (:statusCsv IS NULL OR FIND_IN_SET(b.booking_status, :statusCsv)) " +
	                "AND (:bookedFrom IS NULL OR b.booked_from = :bookedFrom) " +
	                "AND (:fromDate IS NULL OR b.date_of_purchase >= :fromDate) " +
	                "AND (:toDate IS NULL OR b.date_of_purchase <= :toDate) " +
	                "AND (:startDateFromDate IS NULL OR b.start_date >= :startDateFromDate) " +
	                "AND (:startDateToDate IS NULL OR b.start_date <= :startDateToDate) " +
	                "AND ( " +
	                "   :roomName IS NULL OR EXISTS ( " +
	                "       SELECT 1 FROM conference_room cr " +
	                "       WHERE cr.id = b.conference_room_id " +
	                "       AND LOWER(cr.name) = LOWER(:roomName) " +
	                "   ) " +
	                ") " +
	                "AND ( " +
	                "   :search IS NULL OR " +
	                "   CAST(b.id AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   LOWER(b.reference_id) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
	                "   LOWER(b.razorpay_order_id) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
	                "   CAST(b.amount AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   CAST(b.booking_type AS CHAR) LIKE CONCAT('%', :search, '%') OR " +
	                "   EXISTS ( " +
	                "       SELECT 1 FROM conference_room cr " +
	                "       WHERE cr.id = b.conference_room_id " +
	                "       AND LOWER(cr.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
	                "   ) " +
	                ")",
	        nativeQuery = true
	)
	Page<Booking> filterAllBookingsWithTypes(
	        @Param("companyId") String companyId,
	        @Param("bookingTypes") List<String> bookingTypes,
	        @Param("clientId") Long clientId,
	        @Param("referenceId") String referenceId,
	        @Param("statusCsv") String statusCsv,
	        @Param("bookedFrom") String bookedFrom,
	        @Param("roomName") String roomName,
	        @Param("search") String search,
	        @Param("fromDate") LocalDateTime fromDate,
	        @Param("toDate") LocalDateTime toDate,
	        @Param("startDateFromDate") LocalDate startDateFromDate,
	        @Param("startDateToDate") LocalDate startDateToDate,
	        Pageable pageable
	);
	
	@Modifying
	@Transactional
	@Query("UPDATE Booking b SET b.bookingStatus = 'EXPIRED' " +
	       "WHERE (b.bookingStatus = 'ACTIVE' OR b.bookingStatus = 'RESCHEDULED') " +
	       "AND (" +
	       "   (b.expiryDate IS NOT NULL AND b.expiryDate < :today) " +
	       "   OR " +
	       "   (b.expiryDate IS NULL AND b.startDate < :today)" +
	       ")")
	int expirePastBookings(@Param("today") LocalDate today);
	
	@Query("SELECT COALESCE(SUM(b.numberOfPasses), 0) FROM Booking b " +
		       "WHERE b.companyId = :companyId " +
		       "AND TYPE(b) IN (:types) " +
		       "AND (b.bookingStatus = 'ACTIVE' OR b.bookingStatus = 'RESCHEDULED') " +
		       "AND b.startDate = :date " +
		       "AND b.letsWorkCentre.name = :centre " +
		       "AND b.letsWorkCentre.city = :city " +
		       "AND b.letsWorkCentre.state = :state")
		Integer getTotalBookedDayPass(
		        @Param("companyId") String companyId,
		        @Param("types") List<Class<? extends Booking>> types,
		        @Param("centre") String centre,
		        @Param("city") String city,
		        @Param("state") String state,
		        @Param("date") LocalDate date
		);
	
	@Modifying
	@Query("DELETE FROM Booking b WHERE b.bookingStatus = 'DRAFT' AND b.dateOfPurchase <= :expiryTime")
	int deleteExpiredDrafts(@Param("expiryTime") LocalDateTime expiryTime);
	
	@Query("SELECT b FROM Booking b WHERE b.bookingStatus = 'DRAFT' AND b.dateOfPurchase <= :expiryTime")
	List<Booking> findExpiredDrafts(LocalDateTime expiryTime);

//    Optional<Booking> findByBookingCode(String bookingCode);
//
//    @Query("SELECT b FROM AllBookings b WHERE b.companyId = :companyId " +
//            "AND (:email IS NULL OR b.email = :email) " +
//            "AND (:centre IS NULL OR b.letsWorkCentre = :centre) " +
//            "AND (:city IS NULL OR b.city = :city) " +
//            "AND (:state IS NULL OR b.state = :state) " +
//            "AND (:bookingType IS NULL OR b.bookingType = :bookingType) " +
//            "AND (:status IS NULL OR b.currentStatus = :status) " +
//            "AND (:fromDate IS NULL OR b.dateOfBooking >= :fromDate) " +
//            "AND (:toDate IS NULL OR b.dateOfBooking <= :toDate)")
//    Page<Booking> filter(
//            String companyId,
//            String email,
//            String centre,
//            String city,
//            String state,
//            BookingType bookingType,
//            BookingStatus status,
//            LocalDate fromDate,
//            LocalDate toDate,
//            Pageable pageable
//    );

}

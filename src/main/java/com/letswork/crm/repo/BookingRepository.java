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

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	
	
	Optional<Booking> findByReferenceId(String referenceId);
	
	@Query("SELECT b FROM Booking b  LEFT JOIN b.conferenceRoom cr LEFT JOIN b.letsWorkClient c " +
		       "WHERE b.companyId = :companyId " +
		       "AND b.bookingStatus <> 'DRAFT' " +
		       "AND (:clientId IS NULL OR b.letsWorkClient.id = :clientId) " +
		       "AND (:referenceId IS NULL OR b.referenceId = :referenceId) " +
		       "AND (:statuses IS NULL OR b.bookingStatus IN (:statuses)) " + // 💡 Changed to clean IN clause
		       "AND (:bookedFrom IS NULL OR b.bookedFrom = :bookedFrom) " +
		       "AND (:fromDate IS NULL OR b.dateOfPurchase >= :fromDate) " + // 💡 camelCase property names
		       "AND (:toDate IS NULL OR b.dateOfPurchase <= :toDate) " +
		       "AND (:startDateFromDate IS NULL OR b.startDate >= :startDateFromDate) " +
		       "AND (:startDateToDate IS NULL OR b.startDate <= :startDateToDate) " +
		       "AND (:roomName IS NULL OR LOWER(b.conferenceRoom.name) = LOWER(:roomName)) " +
		       "AND (:search IS NULL OR " +
		       "     CAST(b.id AS string) LIKE CONCAT('%', :search, '%') OR " +
		       "     LOWER(b.referenceId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "     LOWER(b.razorpayOrderId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "     CAST(b.amount AS string) LIKE CONCAT('%', :search, '%') OR " +
		       "     LOWER(b.bookingType) LIKE LOWER(CONCAT('%', :search, '%')) OR " + // 💡 Handled discriminator string
		       "     LOWER(b.conferenceRoom.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "     LOWER(b.letsWorkClient.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " + // 🆕 Added
		       "     LOWER(b.letsWorkClient.clientCompanyName) LIKE LOWER(CONCAT('%', :search, '%'))" + // 🆕 Added
		       ")")
		Page<Booking> filterAllBookings(
		        @Param("companyId") String companyId,
		        @Param("clientId") Long clientId,
		        @Param("referenceId") String referenceId,
		        @Param("statuses") List<String> statuses, // 💡 Updated from String statusCsv
		        @Param("bookedFrom") String bookedFrom,
		        @Param("roomName") String roomName,
		        @Param("search") String search,
		        @Param("fromDate") LocalDateTime fromDate,
		        @Param("toDate") LocalDateTime toDate,
		        @Param("startDateFromDate") LocalDate startDateFromDate,
		        @Param("startDateToDate") LocalDate startDateToDate,
		        Pageable pageable
		);

	@Query("SELECT b FROM Booking b LEFT JOIN b.conferenceRoom cr LEFT JOIN b.letsWorkClient c " +
		       "WHERE b.companyId = :companyId " +
		       "AND b.bookingStatus <> 'DRAFT' " +
		       "AND b.bookingType IN (:bookingTypes) " +
		       "AND (:clientId IS NULL OR b.letsWorkClient.id = :clientId) " +
		       "AND (:referenceId IS NULL OR b.referenceId = :referenceId) " +
		       "AND (:statuses IS NULL OR b.bookingStatus IN (:statuses)) " +
		       "AND (:bookedFrom IS NULL OR b.bookedFrom = :bookedFrom) " +
		       "AND (:fromDate IS NULL OR b.dateOfPurchase >= :fromDate) " +
		       "AND (:toDate IS NULL OR b.dateOfPurchase <= :toDate) " +
		       "AND (:startDateFromDate IS NULL OR b.startDate >= :startDateFromDate) " +
		       "AND (:startDateToDate IS NULL OR b.startDate <= :startDateToDate) " +
		       "AND (:roomName IS NULL OR LOWER(b.conferenceRoom.name) = LOWER(:roomName)) " +
		       "AND (:search IS NULL OR " +
		       "     CAST(b.id AS string) LIKE CONCAT('%', :search, '%') OR " +
		       "     LOWER(b.referenceId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "     LOWER(b.razorpayOrderId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "     CAST(b.amount AS string) LIKE CONCAT('%', :search, '%') OR " +
		       "     CAST(b.bookingType AS string) LIKE CONCAT('%', :search, '%') OR " +
		       "     LOWER(b.conferenceRoom.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "     LOWER(b.letsWorkClient.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " + // 🆕 Added
		       "     LOWER(b.letsWorkClient.clientCompanyName) LIKE LOWER(CONCAT('%', :search, '%'))" + // 🆕 Added
		       ")")
	Page<Booking> filterAllBookingsWithTypes(
	        @Param("companyId") String companyId,
	        @Param("bookingTypes") List<String> bookingTypes,
	        @Param("clientId") Long clientId,
	        @Param("referenceId") String referenceId,
	        @Param("statuses") List<String> statuses,
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
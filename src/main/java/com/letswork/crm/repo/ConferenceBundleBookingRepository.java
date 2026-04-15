package com.letswork.crm.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceBundleBooking;
import com.letswork.crm.enums.BookingStatus;

@Repository
public interface ConferenceBundleBookingRepository
        extends JpaRepository<ConferenceBundleBooking, Long> {

    List<ConferenceBundleBooking> findByLetsWorkClientIdAndRemainingHoursGreaterThan(
            Long clientId,
            Integer hours
    );
    
    @Query("SELECT SUM(b.remainingHours) FROM ConferenceBundleBooking b"
			+ "  WHERE b.letsWorkClient.id =:clientId " 
			+  "AND TYPE(b) = 'ConferenceBundleBooking' "
			+ "  AND b.bookingStatus = 'ACTIVE'")
	 Float totalRemainingHoursConferenceBundle(Long clientId);
    
    @Modifying
    @Transactional
    @Query("UPDATE ConferenceBundleBooking b SET b.bookingStatus = 'EXPIRED' " +
           "WHERE b.bookingStatus = 'ACTIVE' " +
           "AND b.expiryDate < :today")
    int expireConferenceBundles(@Param("today") LocalDate today);
    
    @Query("SELECT b FROM Booking b " +
            "WHERE TYPE(b) = ConferenceBundleBooking " +
            "AND b.companyId = :companyId " +
            "AND (:clientId IS NULL OR (b.letsWorkClient IS NOT NULL AND b.letsWorkClient.id = :clientId)) " +
            "AND (:referenceId IS NULL OR b.referenceId = :referenceId) " +
            "AND (:status IS NULL OR b.bookingStatus = :status) " +
            "AND (:fromDate IS NULL OR b.dateOfPurchase >= :fromDate) " +
            "AND (:toDate IS NULL OR b.dateOfPurchase <= :toDate) " +
            "AND (:minHours IS NULL OR b.remainingHours >= :minHours) " +
            "AND (:maxHours IS NULL OR b.remainingHours <= :maxHours) " +
            "AND (:expiryFrom IS NULL OR b.expiryDate >= :expiryFrom) " +
            "AND (:expiryTo IS NULL OR b.expiryDate <= :expiryTo)")
    Page<ConferenceBundleBooking> filter(
            String companyId,
            Long clientId,
            String referenceId,
            BookingStatus status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Float minHours,
            Float maxHours,
            LocalDate expiryFrom,
            LocalDate expiryTo,
            Pageable pageable
    );

}

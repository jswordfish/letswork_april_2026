package com.letswork.crm.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.enums.BookingStatus;

@Repository
public interface ConferenceBookingDirectRepository
        extends JpaRepository<ConferenceBookingDirect, Long> {
	
		
		@Query("SELECT b FROM ConferenceBookingDirect b " +
			       "WHERE b.letsWorkClient.id = :clientId " +
			       "AND b.bookingStatus = 'DRAFT' " +
			       "AND b.startDate = :date")
		List<ConferenceBookingDirect> findExistingDrafts(
		        @Param("clientId") Long clientId, 
		        @Param("date") LocalDate date
		);
	
	    @Query("SELECT b FROM ConferenceBookingDirect b " +
	           "WHERE b.companyId = :companyId " +
	           "AND (:clientId IS NULL OR b.letsWorkClient.id = :clientId) " +
	           "AND (:status IS NULL OR b.bookingStatus = :status) " +
	           "AND (:centre IS NULL OR b.letsWorkCentre.name = :centre) " +
	           "AND (:city IS NULL OR b.letsWorkCentre.city = :city) " +
	           "AND (:state IS NULL OR b.letsWorkCentre.state = :state) " +
	           "AND (:roomName IS NULL OR b.conferenceRoom.name = :roomName) " +
	           "AND (:fromDate IS NULL OR b.dateOfPurchase >= :fromDate) " +
	           "AND (:toDate IS NULL OR b.dateOfPurchase <= :toDate) " +
	           "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
	           "AND (:maxPrice IS NULL OR b.price <= :maxPrice)")
	    Page<ConferenceBookingDirect> filter(
	            @Param("companyId") String companyId,
	            @Param("clientId") Long clientId,
	            @Param("status") BookingStatus status,
	            @Param("centre") String centre,
	            @Param("city") String city,
	            @Param("state") String state,
	            @Param("roomName") String roomName,
	            @Param("fromDate") java.time.LocalDateTime fromDate,
	            @Param("toDate") java.time.LocalDateTime toDate,
	            @Param("minPrice") java.math.BigDecimal minPrice,
	            @Param("maxPrice") java.math.BigDecimal maxPrice,
	            Pageable pageable
	    );
	    
	    Optional<ConferenceBookingDirect> findByIdAndCompanyId(Long id, String companyId);
	
}

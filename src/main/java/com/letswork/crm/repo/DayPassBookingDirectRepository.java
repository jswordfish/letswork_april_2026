package com.letswork.crm.repo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.DayPassBookingDirect;

@Repository
public interface DayPassBookingDirectRepository extends JpaRepository<DayPassBookingDirect, Long> {

//	@Query("SELECT b FROM DayPassBookingDirect b " + "WHERE b.companyId = :companyId "
//			+ "AND (:clientId IS NULL OR b.letsWorkClient.id = :clientId) "
//			+ "AND (:status IS NULL OR b.bookingStatus = :status) "
//			+ "AND (:centre IS NULL OR b.letsWorkCentre.name = :centre) "
//			+ "AND (:city IS NULL OR b.letsWorkCentre.city = :city) "
//			+ "AND (:state IS NULL OR b.letsWorkCentre.state = :state) "
//			+ "AND (:fromDate IS NULL OR b.purchaseDate >= :fromDate) "
//			+ "AND (:toDate IS NULL OR b.purchaseDate <= :toDate) "  )
//	Page<DayPassBookingDirect> getDayPassDirect(@Param("companyId") String companyId, @Param("clientId") Long clientId,
//			@Param("status") BookingStatus status, @Param("centre") String centre, @Param("city") String city,
//			@Param("state") String state, @Param("fromDate") java.time.LocalDateTime fromDate,
//			@Param("toDate") java.time.LocalDateTime toDate,  
//		  Pageable pageable);
	
	@Query("SELECT d FROM DayPassBookingDirect d "
			+ "WHERE d.companyId = :companyId " 
			+ "AND (:centreId IS NULL OR d.letsWorkCentre.id = :centreId) "
			+ "AND (:date IS NULL OR d.dateOfPurchase = :date) "
			+ "AND (:startDate IS NULL OR d.dateOfPurchase >= :startDate) "
			+ "AND (:endDate IS NULL OR d.dateOfPurchase <= :endDate) "
//			+ "AND (:purchaseStart IS NULL OR d.purchaseDate >= :purchaseStart) "
//			+ "AND (:purchaseEnd IS NULL OR d.purchaseDate <= :purchaseEnd) "
			+ "AND (:minPrice IS NULL OR d.discountedPrice >= :minPrice) "
			+ "AND (:maxPrice IS NULL OR d.discountedPrice <= :maxPrice) "
//			+ "AND (:offerId IS NULL OR d.appliedOffer.id = :offerId) "
			+ "AND (:passes IS NULL OR d.numberOfPasses = :passes) ")
		Page<DayPassBookingDirect> searchAllDayPassBookingDirect(
				@Param("companyId") String companyId,
		        @Param("centreId") Long centreId,
		        @Param("date") LocalDateTime date,
		        @Param("startDate") LocalDateTime startDate,
		        @Param("endDate") LocalDateTime endDate,
//		        @Param("purchaseStart") LocalDateTime purchaseStart,
//		        @Param("purchaseEnd") LocalDateTime purchaseEnd,
		        @Param("minPrice") Float minPrice,
		        @Param("maxPrice") Float maxPrice,
//		        @Param("offerId") Long offerId,
		        @Param("passes") Integer passes,
		        Pageable pageable
		);
	
	
	Optional<DayPassBookingDirect> findByIdAndCompanyId(Long id, String companyId);
}

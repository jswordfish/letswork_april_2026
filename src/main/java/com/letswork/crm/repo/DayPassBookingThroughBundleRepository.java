package com.letswork.crm.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.DayPassBookingDirect;
import com.letswork.crm.entities.DayPassBookingThroughBundle;
import com.letswork.crm.enums.BookingStatus;

@Repository
public interface DayPassBookingThroughBundleRepository extends JpaRepository<DayPassBookingThroughBundle, Long> {

	@Query("SELECT d FROM DayPassBookingThroughBundle d " 
			+ "WHERE d.companyId = :companyId " 
			+ "AND (:date IS NULL OR d.dateOfPurchase = :date) "
			+ "AND (:startDate IS NULL OR d.dateOfPurchase >= :startDate) "
			+ "AND (:endDate IS NULL OR d.dateOfPurchase <= :endDate) "
			+ "AND (:centreId IS NULL OR d.letsWorkCentre.id = :centreId) "
			+ "AND (:bundleId IS NULL OR d.dayPassBundleBookingId = :bundleId) "
			+ "AND (:days IS NULL OR d.numberOfPasses = :days)")
	Page<DayPassBookingThroughBundle> searchAllDayPassBookingThroughBundle(@Param("companyId") String companyId, @Param("date") LocalDateTime date,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			@Param("centreId") Long centreId, @Param("bundleId") Long bundleId, @Param("days") Integer days,
			Pageable pageable);
	
	Optional<DayPassBookingThroughBundle> findByIdAndCompanyId(Long id, String companyId);
}

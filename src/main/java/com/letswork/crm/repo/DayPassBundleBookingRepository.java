package com.letswork.crm.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.DayPassBundleBooking;
import com.letswork.crm.enums.BookingStatus;

@Repository
public interface DayPassBundleBookingRepository extends JpaRepository<DayPassBundleBooking, Long> {

//	@Query("SELECT b FROM DayPassBundleBooking b " + "WHERE b.companyId = :companyId "
//			+ "AND (:clientId IS NULL OR b.letsWorkClient.id = :clientId) "
//			+ "AND (:status IS NULL OR b.bookingStatus = :status) "
//			+ "AND (:centre IS NULL OR b.letsWorkCentre.name = :centre) "
//			+ "AND (:city IS NULL OR b.letsWorkCentre.city = :city) "
//			+ "AND (:state IS NULL OR b.letsWorkCentre.state = :state) "
//			+ "AND (:fromDate IS NULL OR b.purchaseDate >= :fromDate) "
//			+ "AND (:toDate IS NULL OR b.purchaseDate <= :toDate) ")
//	Page<DayPassBookingDirect> filterDayPassBundleBooking(@Param("companyId") String companyId,
//			@Param("clientId") Long clientId, @Param("status") BookingStatus status, @Param("centre") String centre,
//			@Param("city") String city, @Param("state") String state,
//			@Param("fromDate") java.time.LocalDateTime fromDate, @Param("toDate") java.time.LocalDateTime toDate,
//			Pageable pageable);

//	@Query("   SELECT d FROM DayPassBundleBooking d"
//			+ "WHERE (:dayPassBundleeId IS NULL OR d.dayPassBundleeId = :dayPassBundleeId)"
//			+ "AND (:expiryFrom IS NULL OR d.expiryDate >= :expiryFrom)"
//			+ "AND (:expiryTo IS NULL OR d.expiryDate <= :expiryTo)"
//			+ "AND (:remainingDays IS NULL OR d.remainingNumberOfDays = :remainingDays)"
//			+ "AND (:offerId IS NULL OR d.appliedOffer.id = :offerId)" + " AND (:paid IS NULL OR d.paid = :paid)"
//			+ " AND (:bundleStatus IS NULL OR d.dayPassBundleStatus = :bundleStatus)"
//			+ "AND (:bookingStatus IS NULL OR d.bookingStatus = :bookingStatus)")
//	Page<DayPassBundleBooking> filterDayPassBundleBooking(@Param("dayPassBundleeId") Long dayPassBundleeId,
//			@Param("expiryFrom") LocalDateTime expiryFrom, @Param("expiryTo") LocalDateTime expiryTo,
//			@Param("remainingDays") Integer remainingDays, @Param("offerId") Long offerId, @Param("paid") Boolean paid,
//			@Param("bundleStatus") DayPassBundleStatus bundleStatus,
//			@Param("bookingStatus") BookingStatus bookingStatus, Pageable pageable);

//	

	@Query("SELECT b FROM DayPassBundleBooking b "
			+ "WHERE b.companyId = :companyId " 
			+ "AND (:bookingStatus IS NULL OR b.bookingStatus = :bookingStatus) "
			+ "AND (:clientId IS NULL OR (b.letsWorkClient IS NOT NULL AND b.letsWorkClient.id = :clientId)) " 
			+ "AND (:dayPassBundleeId IS NULL OR b.dayPassBundleeId = :dayPassBundleeId) "
			+ "AND (:date IS NULL OR b.dateOfPurchase = :date) "
			+ "AND (:startDate IS NULL OR b.dateOfPurchase >= :startDate) "
			+ "AND (:endDate IS NULL OR b.dateOfPurchase <= :endDate) "
			+ "AND (:centreId IS NULL OR b.letsWorkCentre.id = :centreId)"
			+ "AND (:expiryFrom IS NULL OR b.expiryDate >= :expiryFrom)"
			+ "AND (:expiryTo IS NULL OR b.expiryDate <= :expiryTo)"
			+ "AND (:remainingNumberOfDays IS NULL OR b.remainingNumberOfDays = :remainingNumberOfDays) "
			+ " AND (:paid IS NULL OR b.paid = :paid)")
	Page<DayPassBundleBooking> filterDayPassBundleBooking(
			@Param("companyId") String companyId,
			@Param("bookingStatus") BookingStatus bookingStatus,
			@Param("clientId") Long clientId,
			@Param("dayPassBundleeId") Long dayPassBundleeId,
			@Param("date") LocalDateTime date,
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate,
			@Param("centreId") Long centreId,
			@Param("remainingNumberOfDays") Integer remainingNumberOfDays,
			@Param("expiryFrom") LocalDateTime expiryFrom, 
			@Param("expiryTo") LocalDateTime expiryTo,
			@Param("paid") Boolean paid, Pageable pageable);
	
	@Query("SELECT SUM(b.remainingNumberOfDays) FROM DayPassBundleBooking b"
			+ "  WHERE b.letsWorkClient.id =:clientId " 
			+  "AND TYPE(b) = 'DayPassBundleBooking' "
			+ "  AND b.bookingStatus = 'ACTIVE'")
	 Integer totalRemainingDaysDayPass(Long clientId);
 

}

package com.letswork.crm.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.UnifiedBooking;
import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.BookingType;


@Repository
public interface UnifiedBookingRepository
        extends JpaRepository<UnifiedBooking, String> {

	
	    @Query("SELECT u FROM UnifiedBooking u " +
	           "WHERE u.companyId = :companyId " +
	           "AND (:email IS NULL OR u.email = :email) " +
	           "AND (:centre IS NULL OR u.letsWorkCentre = :centre) " +
	           "AND (:city IS NULL OR u.city = :city) " +
	           "AND (:state IS NULL OR u.state = :state) " +
	           "AND (:status IS NULL OR u.currentStatus = :status) " +
	           "AND (:bookingType IS NULL OR u.bookingType = :bookingType) " +
	           "AND (:roomName IS NULL OR u.roomName = :roomName) " +
	           "AND (:fromDate IS NULL OR u.dateOfBooking >= :fromDate) " +
	           "AND (:toDate IS NULL OR u.dateOfBooking <= :toDate)")
	    Page<UnifiedBooking> filter(
	            @Param("companyId") String companyId,
	            @Param("email") String email,
	            @Param("centre") String centre,
	            @Param("city") String city,
	            @Param("state") String state,
	            @Param("fromDate") java.time.LocalDate fromDate,
	            @Param("toDate") java.time.LocalDate toDate,
	            @Param("roomName") String roomName,
	            @Param("status") BookingStatus status,
	            @Param("bookingType") BookingType bookingType,
	            Pageable pageable
	    );
			
}

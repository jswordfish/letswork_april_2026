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

import com.letswork.crm.entities.BookConferenceRoom;
import com.letswork.crm.enums.BookingStatus;

@Repository
public interface BookConferenceRoomRepository
        extends JpaRepository<BookConferenceRoom, Long> {

    List<BookConferenceRoom> findByCompanyId(String companyId);
    
    Optional<BookConferenceRoom> findByIdAndCompanyId(Long id, String companyId);

    List<BookConferenceRoom> findByEmailAndCompanyId(
            String email,
            String companyId
    );

    Optional<BookConferenceRoom> findByBookingCode(String bookingCode);

    @Query(
    	    "SELECT b FROM BookConferenceRoom b " +
    	    "WHERE b.companyId = :companyId " +
    	    "AND (:email IS NULL OR b.email = :email) " +
    	    "AND (:centre IS NULL OR b.letsWorkCentre = :centre) " +
    	    "AND (:city IS NULL OR b.city = :city) " +
    	    "AND (:state IS NULL OR b.state = :state) " +
    	    "AND (:fromDate IS NULL OR b.dateOfBooking >= :fromDate) " +
    	    "AND (:toDate IS NULL OR b.dateOfBooking <= :toDate) " +
    	    "AND (:roomName IS NULL OR b.roomName = :roomName) " +
    	    "AND (:currentStatus IS NULL OR b.currentStatus = :currentStatus)"
    	)
    	Page<BookConferenceRoom> filter(
    	        @Param("companyId") String companyId,
    	        @Param("email") String email,
    	        @Param("centre") String centre,
    	        @Param("city") String city,
    	        @Param("state") String state,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate,
    	        @Param("roomName") String roomName,
    	        @Param("currentStatus") BookingStatus currentStatus, // ⭐ NEW 
    	        Pageable pageable
    	);
    
    @Query("SELECT b FROM BookConferenceRoom b WHERE b.companyId = :companyId " +
            "AND (:email IS NULL OR b.email = :email) " +
            "AND (:centre IS NULL OR b.letsWorkCentre = :centre) " +
            "AND (:city IS NULL OR b.city = :city) " +
            "AND (:state IS NULL OR b.state = :state) " +
            "AND (:fromDate IS NULL OR b.dateOfBooking >= :fromDate) " +
            "AND (:toDate IS NULL OR b.dateOfBooking <= :toDate) " +
            "AND (:roomName IS NULL OR b.roomName = :roomName) " +
            "AND (:currentStatus IS NULL OR b.currentStatus = :currentStatus)")
    List<BookConferenceRoom> filterForUnified(
            String companyId,
            String email,
            String centre,
            String city,
            String state,
            LocalDate fromDate,
            LocalDate toDate,
            String roomName,
            BookingStatus currentStatus
    );
    
}

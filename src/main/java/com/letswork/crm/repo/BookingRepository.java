package com.letswork.crm.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Booking;



@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	
    Optional<Booking> findByBookingCode(String bookingCode);
    
    
    
    @Query("SELECT b FROM Booking b " +
            "WHERE b.conferenceRoomName = :roomName " +
            "AND b.letsWorkCentre = :letsWorkCentre " +
            "AND b.companyId = :companyId " +
            "AND b.isActive = true " +
            "AND ((b.startTime < :endTime AND b.endTime > :startTime))")
     List<Booking> findConflictingBookings(@Param("roomName") String roomName,
                                          @Param("letsWorkCentre") String letsWorkCentre,
                                          @Param("companyId") String companyId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);
    
    List<Booking> findByLetsWorkCentreAndCityAndStateAndCompanyId(String letsWorkCentre, String city, String state, String companyId);



	Page<Booking> findByCompanyId(String companyId, Pageable pageable);
    
}

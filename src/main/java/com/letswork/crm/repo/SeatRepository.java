package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Seat;
import com.letswork.crm.enums.SeatType;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
	
    Page<Seat> findByCompanyIdAndLocation(String companyId, String location, Pageable pageable);
    
    Optional<Seat> findBySeatTypeAndCompanyIdAndLocation(SeatType seatType, String companyId, String location);
    
    Optional<Seat> findBySeatTypeAndCompanyIdAndLocationAndSeatNumber(SeatType seatType, String companyId, String location, int seatNumber);
    
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.location = :location AND s.seatType = :seatType AND s.companyId = :companyId")
    long countByCompanyIdAndLocationAndSeatType(@Param("companyId") String companyId,
                                                @Param("location") String location,
                                                @Param("seatType") SeatType seatType);
    
}

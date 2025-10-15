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
	
    Page<Seat> findByCompanyIdAndLetsWorkCentre(String companyId, String letsWorkCentre, Pageable pageable);
    
    Optional<Seat> findBySeatTypeAndCompanyIdAndLetsWorkCentre(SeatType seatType, String companyId, String letsWorkCentre);
    
    Optional<Seat> findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumber(SeatType seatType, String companyId, String letsWorkCentre, String seatNumber);
    
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.letsWorkCentre = :letsWorkCentre AND s.seatType = :seatType AND s.companyId = :companyId")
    long countByCompanyIdAndLetsWorkCentreAndSeatType(@Param("companyId") String companyId,
                                                @Param("letsWorkCentre") String letsWorkCentre,
                                                @Param("seatType") SeatType seatType);
    
}

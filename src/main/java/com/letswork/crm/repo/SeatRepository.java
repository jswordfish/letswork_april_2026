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
	
	Page<Seat> findByCompanyIdAndLetsWorkCentreAndCityAndState(String companyId, String letsWorkCentre, String city, String state, Pageable pageable);

	Optional<Seat> findBySeatTypeAndCompanyIdAndLetsWorkCentreAndCityAndState(SeatType seatType, String companyId, String letsWorkCentre, String city, String state);

	Optional<Seat> findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumberAndCityAndState(SeatType seatType, String companyId, String letsWorkCentre, String seatNumber, String city, String state);

	@Query("SELECT COUNT(s) FROM Seat s WHERE s.letsWorkCentre = :letsWorkCentre AND s.seatType = :seatType AND s.companyId = :companyId AND s.city = :city AND s.state = :state")
	long countByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(@Param("companyId") String companyId,
	                                                                 @Param("letsWorkCentre") String letsWorkCentre,
	                                                                 @Param("seatType") SeatType seatType,
	                                                                 @Param("city") String city,
	                                                                 @Param("state") String state);
    
}

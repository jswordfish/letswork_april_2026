package com.letswork.crm.repo;

import java.util.List;
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
	
	

	Optional<Seat> findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumberAndCityAndState(SeatType seatType, String companyId, String letsWorkCentre, String seatNumber, String city, String state);

	Optional<Seat> findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumberAndCityAndStateAndPublishedTrue(
	        SeatType seatType,
	        String companyId,
	        String letsWorkCentre,
	        String seatNumber,
	        String city,
	        String state);
	
	Page<Seat> findByCompanyId(String companyId, Pageable pageable);

    @Query("SELECT s FROM Seat s WHERE s.companyId = :companyId "
         + "AND (:letsWorkCentre IS NULL OR s.letsWorkCentre = :letsWorkCentre) "
         + "AND (:city IS NULL OR s.city = :city) "
         + "AND (:state IS NULL OR s.state = :state)")
    Page<Seat> findByFilters(@Param("companyId") String companyId,
                             @Param("letsWorkCentre") String letsWorkCentre,
                             @Param("city") String city,
                             @Param("state") String state,
                             Pageable pageable);
	
	@Query("SELECT COUNT(s) FROM Seat s WHERE s.letsWorkCentre = :letsWorkCentre AND s.seatType = :seatType AND s.companyId = :companyId AND s.city = :city AND s.state = :state")
	long countByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(@Param("companyId") String companyId,
	                                                                 @Param("letsWorkCentre") String letsWorkCentre,
	                                                                 @Param("seatType") SeatType seatType,
	                                                                 @Param("city") String city,
	                                                                 @Param("state") String state);
	
	@Query("SELECT s FROM Seat s WHERE s.letsWorkCentre = :letsWorkCentre AND s.seatType = :seatType " +
		       "AND s.companyId = :companyId AND s.city = :city AND s.state = :state AND s.published = true")
		List<Seat> findAllByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(
		        @Param("companyId") String companyId,
		        @Param("letsWorkCentre") String letsWorkCentre,
		        @Param("seatType") SeatType seatType,
		        @Param("city") String city,
		        @Param("state") String state);
	
	@Query("SELECT s FROM Seat s WHERE s.letsWorkCentre = :letsWorkCentre" +
		       "AND s.companyId = :companyId AND s.city = :city AND s.state = :state AND s.published = true")
		List<Seat> findAllByCompanyIdAndLetsWorkCentreAndCityAndState(
		        @Param("companyId") String companyId,
		        @Param("letsWorkCentre") String letsWorkCentre,
		        @Param("city") String city,
		        @Param("state") String state);
	
	@Query("SELECT s FROM Seat s WHERE s.letsWorkCentre = :letsWorkCentre AND s.companyId = :companyId AND s.city = :city AND s.state = :state")
    Page<Seat> findByLetsWorkCentreAndCompanyIdAndCityAndState(@Param("letsWorkCentre") String letsWorkCentre, @Param("companyId") String companyId, @Param("city") String city, @Param("state") String state, Pageable pageable);
	
	@Query("SELECT s FROM Seat s WHERE s.letsWorkCentre = :letsWorkCentre AND s.companyId = :companyId AND s.city = :city AND s.state = :state AND s.published = true")
	Page<Seat> findPublishedSeatsByLetsWorkCentreAndCompanyIdAndCityAndState(
	        @Param("letsWorkCentre") String letsWorkCentre,
	        @Param("companyId") String companyId,
	        @Param("city") String city,
	        @Param("state") String state,
	        Pageable pageable);
	
}

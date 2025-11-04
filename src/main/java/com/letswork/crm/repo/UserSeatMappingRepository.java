package com.letswork.crm.repo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.SeatKey;
import com.letswork.crm.entities.UserSeatMapping;
import com.letswork.crm.enums.SeatType;

@Repository
public interface UserSeatMappingRepository extends JpaRepository<UserSeatMapping, Long> {

	Page<UserSeatMapping> findByCompanyIdAndLetsWorkCentreAndCityAndState(
	        String companyId, String letsWorkCentre, String city, String state, Pageable pageable);
	
	
	@Query("SELECT new com.letswork.crm.entities.SeatKey(u.letsWorkCentre, u.city, u.state, u.companyId, u.seatType, u.seatNumber) " +
		       "FROM UserSeatMapping u " +
		       "WHERE u.companyId = :companyId AND u.letsWorkCentre = :letsWorkCentre " +
		       "AND u.city = :city AND u.state = :state")
		List<SeatKey> findSeatKeysByCompanyIdAndLetsWorkCentreAndCityAndState(
		        @Param("companyId") String companyId,
		        @Param("letsWorkCentre") String letsWorkCentre,
		        @Param("city") String city,
		        @Param("state") String state);
	
	

	Optional<UserSeatMapping> findByEmailAndCompanyIdAndLetsWorkCentreAndCityAndState(
	        String email, String companyId, String letsWorkCentre, String city, String state);

	@Query("SELECT COUNT(u) FROM UserSeatMapping u WHERE u.letsWorkCentre = :letsWorkCentre AND u.seatType = :seatType AND u.companyId = :companyId AND u.city = :city AND u.state = :state")
	long countByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(
	        @Param("companyId") String companyId,
	        @Param("letsWorkCentre") String letsWorkCentre,
	        @Param("seatType") SeatType seatType,
	        @Param("city") String city,
	        @Param("state") String state);
	
	@Query("SELECT u FROM UserSeatMapping u WHERE u.letsWorkCentre = :letsWorkCentre AND u.companyId = :companyId AND u.city = :city AND u.state = :state")
    Page<UserSeatMapping> findByLetsWorkCentreAndCompanyIdAndCityAndState(@Param("letsWorkCentre") String letsWorkCentre, @Param("companyId") String companyId, @Param("city") String city, @Param("state") String state, Pageable pageable);
    
	
	@Query("SELECT u FROM UserSeatMapping u WHERE " +
		       "u.seatNumber = :seatNumber AND " +
		       "u.seatType = :seatType AND " +
		       "u.letsWorkCentre = :letsWorkCentre AND " +
		       "u.companyId = :companyId AND " +
		       "u.city = :city AND " +
		       "u.state = :state")
		Optional<UserSeatMapping> findBySeatNumberAndSeatTypeAndLetsWorkCentreAndCompanyIdAndCityAndState(
		        @Param("seatNumber") String seatNumber,
		        @Param("seatType") SeatType seatType,
		        @Param("letsWorkCentre") String letsWorkCentre,
		        @Param("companyId") String companyId,
		        @Param("city") String city,
		        @Param("state") String state);
	
	@Query("SELECT u.seatNumber FROM UserSeatMapping u WHERE u.letsWorkCentre = :letsWorkCentre AND u.seatType = :seatType AND u.companyId = :companyId AND u.city = :city AND u.state = :state")
	List<String> findSeatNumbersByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(
	        @Param("companyId") String companyId,
	        @Param("letsWorkCentre") String letsWorkCentre,
	        @Param("seatType") SeatType seatType,
	        @Param("city") String city,
	        @Param("state") String state);
	
}
package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Cabin;

@Repository
public interface CabinRepository extends JpaRepository<Cabin, Long> {
    
	Optional<Cabin> findByCabinNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
	        String cabinName, String letsWorkCentre, String companyId, String city, String state);
	
	Cabin findByCabinNameAndCompanyIdAndLetsWorkCentreAndCityAndState(
	        String cabinName, String companyId, String letsWorkCentre, String city, String state);

	boolean existsByCabinNameAndCompanyIdAndLetsWorkCentreAndCityAndState(
	        String cabinName, String companyId, String letsWorkCentre, String city, String state);
	
	@Query("SELECT c FROM Cabin c WHERE c.letsWorkCentre = :letsWorkCentre AND c.companyId = :companyId AND c.city = :city AND c.state = :state")
    Page<Cabin> findByLetsWorkCentreAndCompanyIdAndCityAndState(@Param("letsWorkCentre") String letsWorkCentre, @Param("companyId") String companyId, @Param("city") String city, @Param("state") String state, Pageable pageable);
    
	@Query(
		       "SELECT c FROM Cabin c " +
		       "WHERE c.companyId = :companyId " +
		       "AND (:letsWorkCentre IS NULL OR c.letsWorkCentre = :letsWorkCentre) " +
		       "AND (:city IS NULL OR c.city = :city) " +
		       "AND (:state IS NULL OR c.state = :state) " +
		       "AND ( " +
		       "     :search IS NULL " +
		       "     OR c.cabinName LIKE %:search% " +
		       "     OR c.cabinNumber LIKE %:search% " +
		       "     OR c.city LIKE %:search% " +
		       "     OR c.state LIKE %:search% " +
		       "     OR c.letsWorkCentre LIKE %:search% " +
		       ")"
		)
		Page<Cabin> searchCabins(@Param("companyId") String companyId,
		                         @Param("letsWorkCentre") String letsWorkCentre,
		                         @Param("city") String city,
		                         @Param("state") String state,
		                         @Param("search") String search,
		                         Pageable pageable);
	
}
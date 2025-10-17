package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Cabin;

@Repository
public interface CabinRepository extends JpaRepository<Cabin, Long> {
    
	Optional<Cabin> findByCabinNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
	        String cabinName, String letsWorkCentre, String companyId, String city, String state);

	boolean existsByCabinNameAndCompanyIdAndLetsWorkCentreAndCityAndState(
	        String cabinName, String companyId, String letsWorkCentre, String city, String state);
    
}
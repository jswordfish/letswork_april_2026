package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.WifiRouter;



@Repository
public interface WifiRouterRepository extends JpaRepository<WifiRouter, Long> {

    
	Optional<WifiRouter> findByWifiNameAndCompanyIdAndCityAndState(String wifiName, String companyId, String city, String state);

	@Query("SELECT w FROM WifiRouter w WHERE w.letsWorkCentre = :letsWorkCentre AND w.companyId = :companyId AND w.city = :city AND w.state = :state")
	Page<WifiRouter> findByLetsWorkCentreAndCompanyIdAndCityAndState(@Param("letsWorkCentre") String letsWorkCentre,
	                                                                 @Param("companyId") String companyId,
	                                                                 @Param("city") String city,
	                                                                 @Param("state") String state,
	                                                                 Pageable pageable);

	@Query("SELECT w FROM WifiRouter w WHERE w.wifiName = :wifiName AND w.letsWorkCentre = :letsWorkCentre AND w.companyId = :companyId AND w.city = :city AND w.state = :state")
	WifiRouter findByNameLetsWorkCentreAndCompanyAndCityAndState(@Param("wifiName") String wifiName,
	                                                             @Param("letsWorkCentre") String letsWorkCentre,
	                                                             @Param("companyId") String companyId,
	                                                             @Param("city") String city,
	                                                             @Param("state") String state);
}

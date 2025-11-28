package com.letswork.crm.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.LetsworkUser;
import com.letswork.crm.entities.WifiRouter;
import com.letswork.crm.entities.WifiRouterMappingToClient;



@Repository
public interface WifiRouterMappingToClientRepository extends JpaRepository<WifiRouterMappingToClient, Long> {

    
	WifiRouterMappingToClient findByWifiNameAndClientEmailAndLetsWorkCentre(String wifiName, String clientEmail, String letsWorkCentre);

	@Query("SELECT c FROM LetsworkUser c " +
	           "JOIN WifiRouterMappingToClient m ON c.email = m.clientEmail " +
	           "WHERE m.wifiName = :wifiName AND m.letsWorkCentre = :letsWorkCentre AND m.companyId = :companyId")
	    Page<LetsworkUser> findClientsByWifi(@Param("wifiName") String wifiName,
	                                   @Param("letsWorkCentre") String letsWorkCentre,
	                                   @Param("companyId") String companyId,
	                                   Pageable pageable);
	
	@Query("SELECT w FROM WifiRouter w " +
	           "JOIN WifiRouterMappingToClient m ON w.wifiName = m.wifiName AND w.letsWorkCentre = m.letsWorkCentre " +
	           "WHERE m.clientEmail = :clientEmail AND m.companyId = :companyId")
	    Page<WifiRouter> findRoutersByClient(
	                                         @Param("clientEmail") String clientEmail,
	                                         @Param("companyId") String companyId,
	                                         Pageable pageable);
	
}

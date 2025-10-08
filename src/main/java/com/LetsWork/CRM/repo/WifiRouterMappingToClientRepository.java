package com.LetsWork.CRM.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LetsWork.CRM.entities.Client;
import com.LetsWork.CRM.entities.WifiRouter;
import com.LetsWork.CRM.entities.WifiRouterMappingToClient;

@Repository
public interface WifiRouterMappingToClientRepository extends JpaRepository<WifiRouterMappingToClient, Long> {

    
	WifiRouterMappingToClient findByWifiNameAndClientEmailAndLocation(String wifiName, String clientEmail, String location);

	@Query("SELECT c FROM Client c " +
	           "JOIN WifiRouterMappingToClient m ON c.name = m.clientName AND c.email = m.clientEmail " +
	           "WHERE m.wifiName = :wifiName AND m.location = :location AND m.companyId = :companyId")
	    Page<Client> findClientsByWifi(@Param("wifiName") String wifiName,
	                                   @Param("location") String location,
	                                   @Param("companyId") String companyId,
	                                   Pageable pageable);
	
	@Query("SELECT w FROM WifiRouter w " +
	           "JOIN WifiRouterMappingToClient m ON w.wifiName = m.wifiName AND w.location = m.location " +
	           "WHERE m.clientName = :clientName AND m.clientEmail = :clientEmail AND m.companyId = :companyId")
	    Page<WifiRouter> findRoutersByClient(@Param("clientName") String clientName,
	                                         @Param("clientEmail") String clientEmail,
	                                         @Param("companyId") String companyId,
	                                         Pageable pageable);
	
}

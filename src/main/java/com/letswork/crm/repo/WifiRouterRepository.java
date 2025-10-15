package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.WifiRouter;



@Repository
public interface WifiRouterRepository extends JpaRepository<WifiRouter, Long> {

    
    Optional<WifiRouter> findByWifiNameAndCompanyId(String wifiName, String companyId);

    
    @Query("SELECT w FROM WifiRouter w WHERE w.letsWorkCentre = :letsWorkCentre AND w.companyId = :companyId")
    Page<WifiRouter> findByLetsWorkCentre(String letsWorkCentre, String companyId, Pageable pageable);

    
    @Query("SELECT w FROM WifiRouter w WHERE w.wifiName = :wifiName AND w.letsWorkCentre = :letsWorkCentre AND w.companyId = :companyId")
    WifiRouter findByNameLetsWorkCentreAndCompany(String wifiName, String letsWorkCentre, String companyId);
}

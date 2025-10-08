package com.LetsWork.CRM.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.LetsWork.CRM.entities.WifiRouter;



@Repository
public interface WifiRouterRepository extends JpaRepository<WifiRouter, Long> {

    
    Optional<WifiRouter> findByWifiNameAndCompanyId(String wifiName, String companyId);

    
    @Query("SELECT w FROM WifiRouter w WHERE w.location = :location AND w.companyId = :companyId")
    Page<WifiRouter> findByLocation(String location, String companyId, Pageable pageable);

    
    @Query("SELECT w FROM WifiRouter w WHERE w.wifiName = :wifiName AND w.location = :location AND w.companyId = :companyId")
    WifiRouter findByNameLocationAndCompany(String wifiName, String location, String companyId);
}

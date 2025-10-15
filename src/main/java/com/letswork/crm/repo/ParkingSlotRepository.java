package com.letswork.crm.repo;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ParkingSlot;



@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    ParkingSlot findByNameAndCompanyId(String name, String companyId);

    
    @Query("SELECT p FROM ParkingSlot p WHERE p.letsWorkCentre = :letsWorkCentre AND p.companyId = :companyId")
    Page<ParkingSlot> findByLetsWorkCentre(String letsWorkCentre, String companyId, Pageable pageable);

    
    @Query("SELECT p FROM ParkingSlot p WHERE p.name = :name AND p.letsWorkCentre = :letsWorkCentre AND p.companyId = :companyId")
    ParkingSlot findByNameLetsWorkCentreAndCompany(String name, String letsWorkCentre, String companyId);
    
}

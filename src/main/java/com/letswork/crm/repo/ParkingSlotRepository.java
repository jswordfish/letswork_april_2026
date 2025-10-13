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

    
    @Query("SELECT p FROM ParkingSlot p WHERE p.location = :location AND p.companyId = :companyId")
    Page<ParkingSlot> findByLocation(String location, String companyId, Pageable pageable);

    
    @Query("SELECT p FROM ParkingSlot p WHERE p.name = :name AND p.location = :location AND p.companyId = :companyId")
    ParkingSlot findByNameLocationAndCompany(String name, String location, String companyId);
    
}

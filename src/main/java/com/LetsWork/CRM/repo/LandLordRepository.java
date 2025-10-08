package com.LetsWork.CRM.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LetsWork.CRM.entities.LandLord;

@Repository
public interface LandLordRepository extends JpaRepository<LandLord, Long> {
	
    Optional<LandLord> findByGstNumber(String gstNumber);

    
    Page<LandLord> findByCompanyId(String companyId, Pageable pageable);
    
}

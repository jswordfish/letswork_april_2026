package com.letswork.crm.repo;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.LetsWorkCentre;



@Repository
public interface LetsWorkCentreRepository extends JpaRepository<LetsWorkCentre, Long> {

    
	LetsWorkCentre findByName(String name);
    
    @Query("SELECT l FROM LetsWorkCentre l WHERE l.name = :name AND l.companyId = :companyId")
    LetsWorkCentre findByNameAndCompanyId(@Param("name") String name,
                                    @Param("companyId") String companyId);
    
    Page<LetsWorkCentre> findAll(Pageable pageable);
    
}

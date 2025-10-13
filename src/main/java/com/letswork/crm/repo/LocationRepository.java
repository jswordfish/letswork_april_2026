package com.letswork.crm.repo;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Location;



@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    
    Location findByName(String name);
    
    @Query("SELECT l FROM Location l WHERE l.name = :name AND l.companyId = :companyId")
    Location findByNameAndCompanyId(@Param("name") String name,
                                    @Param("companyId") String companyId);
    
    Page<Location> findAll(Pageable pageable);
    
}

package com.letswork.crm.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Solutions;

@Repository
public interface SolutionsRepository extends JpaRepository<Solutions, Long>{
	
    Solutions findByNameAndLetsWorkCentreAndCompanyId(
            String name,
            String letsWorkCentre,
            String companyId
    );

    Page<Solutions> findByLetsWorkCentreAndCompanyId(
            String letsWorkCentre,
            String companyId,
            Pageable pageable
    );

    Page<Solutions> findByCompanyId(
            String companyId,
            Pageable pageable
    );
    
    @Query("SELECT s FROM Solutions s WHERE s.companyId = :companyId " +
    	       "AND (:letsWorkCentre IS NULL OR s.letsWorkCentre = :letsWorkCentre) " +
    	       "AND (:search IS NULL OR " +
    	       "LOWER(s.name) LIKE :search OR " +
    	       "LOWER(s.price) LIKE :search OR " +
    	       "LOWER(s.letsWorkCentre) LIKE :search OR " +
    	       "LOWER(s.city) LIKE :search OR " +
    	       "LOWER(s.state) LIKE :search OR " +
    	       "LOWER(s.amenities) LIKE :search OR " +
    	       "LOWER(s.solutionType.name) LIKE :search)")
    	Page<Solutions> searchSolutions(
    	    @Param("companyId") String companyId,
    	    @Param("letsWorkCentre") String letsWorkCentre,
    	    @Param("search") String search, 
    	    Pageable pageable
    	);

}

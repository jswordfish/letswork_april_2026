package com.letswork.crm.repo;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.LetsWorkCentre;



@Repository
public interface LetsWorkCentreRepository extends JpaRepository<LetsWorkCentre, Long> {

    
//	@Query("SELECT l FROM LetsWorkCentre l WHERE l.name = :name AND l.companyId = :companyId")
//    LetsWorkCentre findByNameAndCompanyId(@Param("name") String name,
//                                    @Param("companyId") String companyId);
	
	@Query("SELECT l FROM LetsWorkCentre l WHERE l.name = :name AND l.companyId = :companyId AND l.city = :city AND l.state = :state")
	LetsWorkCentre findByNameAndCompanyIdAndCityAndState(@Param("name") String name,
	                                                     @Param("companyId") String companyId,
	                                                     @Param("city") String city,
	                                                     @Param("state") String state);
    
	@Query("SELECT l.amenities FROM LetsWorkCentre l WHERE l.companyId = :companyId AND l.amenities IS NOT NULL")
	List<String> findAllAmenitiesByCompanyId(@Param("companyId") String companyId);
    
    @Query("SELECT l FROM LetsWorkCentre l WHERE l.companyId = :companyId")
    Page<LetsWorkCentre> findAllByCompanyId(@Param("companyId") String companyId, Pageable pageable);
    
    @Query("SELECT l FROM LetsWorkCentre l WHERE l.companyId = :companyId")
    List<LetsWorkCentre> findAllByCompanyId(@Param("companyId") String companyId);
    
    
}

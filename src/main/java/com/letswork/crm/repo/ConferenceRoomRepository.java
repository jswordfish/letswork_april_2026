package com.letswork.crm.repo;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceRoom;



@Repository
public interface ConferenceRoomRepository extends JpaRepository<ConferenceRoom, Long> {

	
//    List<ConferenceRoom> findByAvailable(Boolean available);
//    
//    Page<ConferenceRoom> findByAvailable(Boolean available, Pageable pageable);

	@Query("SELECT c FROM ConferenceRoom c WHERE c.letsWorkCentre = :letsWorkCentre AND c.companyId = :companyId AND c.city = :city AND c.state = :state")
	List<ConferenceRoom> findByLetsWorkCentreAndCompanyIdAndCityAndState(@Param("letsWorkCentre") String letsWorkCentre,
	                                                                     @Param("companyId") String companyId,
	                                                                     @Param("city") String city,
	                                                                     @Param("state") String state);

	@Query("SELECT c FROM ConferenceRoom c WHERE c.name = :name AND c.letsWorkCentre = :letsWorkCentre AND c.companyId = :companyId AND c.city = :city AND c.state = :state")
	ConferenceRoom findByNameAndLetsWorkCentreAndCompanyIdAndCityAndState(@Param("name") String name,
	                                                                      @Param("letsWorkCentre") String letsWorkCentre,
	                                                                      @Param("companyId") String companyId,
	                                                                      @Param("city") String city,
	                                                                      @Param("state") String state);

	@Query("SELECT c FROM ConferenceRoom c WHERE c.letsWorkCentre = :letsWorkCentre AND c.companyId = :companyId AND c.city = :city AND c.state = :state")
	Page<ConferenceRoom> findByLetsWorkCentreAndCompanyIdAndCityAndState(@Param("letsWorkCentre") String letsWorkCentre,
	                                                                     @Param("companyId") String companyId,
	                                                                     @Param("city") String city,
	                                                                     @Param("state") String state,
	                                                                     Pageable pageable);
    
    

}

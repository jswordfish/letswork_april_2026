package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.DayPassBundle;

@Repository
public interface DayPassBundleRepository
        extends JpaRepository<DayPassBundle, Long> {
	
	@Query("SELECT d FROM DayPassBundle d " +
	 	       "WHERE d.companyId = :companyId " +
	 	       "AND d.letsWorkCentre.name = :letsWorkCentre " +
	 	       "AND d.letsWorkCentre.city = :city " +
	 	       "AND d.letsWorkCentre.state = :state ")
	 	       
    List<DayPassBundle> findByLetsWorkCentreAndCompanyIdAndCityAndState(
            @Param("letsWorkCentre")String letsWorkCentre,
            @Param("companyId")String companyId,
            @Param("city")String city,
            @Param("state")String state
    );
    
	@Query("SELECT d FROM DayPassBundle d " +
	 	       "WHERE d.companyId = :companyId " +
	 	       "AND d.letsWorkCentre.name = :letsWorkCentre " +
	 	       "AND d.letsWorkCentre.city = :city " +
	 	       "AND d.letsWorkCentre.state = :state " +
	 	       "AND d.numberOfDays = :numberOfDays")
    DayPassBundle findByLetsWorkCentreAndCompanyIdAndCityAndStateAndNumberOfDays(@Param("letsWorkCentre")String letsWorkCentre,
    		@Param("companyId")String companyId,
    		@Param("city")String city,
    		@Param("state")String state,
    		@Param("numberOfDays")String numberOfDays);
	
	//
	
	@Query("SELECT d FROM DayPassBundle d " +
	 	       "WHERE d.companyId = :companyId " +
	 	       "AND d.letsWorkCentre.name = :letsWorkCentre " +
	 	       "AND d.letsWorkCentre.city = :city " +
	 	       "AND d.letsWorkCentre.state = :state ")
	 	       
 DayPassBundle findByLetsWorkCentreAndCompanyIdAndCityAndState2(
         @Param("letsWorkCentre")String letsWorkCentre,
         @Param("companyId")String companyId,
         @Param("city")String city,
         @Param("state")String state
 );

    List<DayPassBundle> findAllByCompanyId(String companyId);
}

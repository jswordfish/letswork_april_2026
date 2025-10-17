package com.letswork.crm.repo;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ParkingSlot;



@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

	ParkingSlot findByNameAndCompanyIdAndCityAndState(String name, String companyId, String city, String state);

	@Query("SELECT p FROM ParkingSlot p WHERE p.letsWorkCentre = :letsWorkCentre AND p.companyId = :companyId AND p.city = :city AND p.state = :state")
	Page<ParkingSlot> findByLetsWorkCentreAndCompanyIdAndCityAndState(@Param("letsWorkCentre") String letsWorkCentre,
	                                                                  @Param("companyId") String companyId,
	                                                                  @Param("city") String city,
	                                                                  @Param("state") String state,
	                                                                  Pageable pageable);

	@Query("SELECT p FROM ParkingSlot p WHERE p.name = :name AND p.letsWorkCentre = :letsWorkCentre AND p.companyId = :companyId AND p.city = :city AND p.state = :state")
	ParkingSlot findByNameLetsWorkCentreAndCompanyAndCityAndState(@Param("name") String name,
	                                                              @Param("letsWorkCentre") String letsWorkCentre,
	                                                              @Param("companyId") String companyId,
	                                                              @Param("city") String city,
	                                                              @Param("state") String state);
    
}

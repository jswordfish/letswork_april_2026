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
	
	@Query(
	        "SELECT r FROM ConferenceRoom r " +
	        "WHERE r.companyId = :companyId " +
	        "AND (:letsWorkCentre IS NULL OR r.letsWorkCentre = :letsWorkCentre) " +
	        "AND (:city IS NULL OR r.city = :city) " +
	        "AND (:state IS NULL OR r.state = :state) " +
	        "AND ( " +
	        "     :search IS NULL " +
	        "     OR r.name LIKE %:search% " +
	        "     OR r.letsWorkCentre LIKE %:search% " +
	        "     OR r.city LIKE %:search% " +
	        "     OR r.state LIKE %:search% " +
	        ")"
	)
	Page<ConferenceRoom> searchConferenceRooms(@Param("companyId") String companyId,
	                                           @Param("letsWorkCentre") String letsWorkCentre,
	                                           @Param("city") String city,
	                                           @Param("state") String state,
	                                           @Param("search") String search,
	                                           Pageable pageable);

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
	
	@Query("SELECT c FROM ConferenceRoom c WHERE c.companyId = :companyId")
    Page<ConferenceRoom> findAllByCompanyId(@Param("companyId") String companyId, Pageable pageable);
    
    

}

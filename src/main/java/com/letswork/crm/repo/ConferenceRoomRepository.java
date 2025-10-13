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

    @Query("SELECT c FROM ConferenceRoom c WHERE c.location = :location AND c.companyId = :companyId")
    List<ConferenceRoom> findByLocationAndCompanyId(@Param("location") String location,
                                                   @Param("companyId") String companyId);
    
    ConferenceRoom findByName(String roomName);
    
    @Query("SELECT c FROM ConferenceRoom c WHERE c.name = :name AND c.location = :location AND c.companyId = :companyId")
    ConferenceRoom findByNameAndLocationAndCompanyId(@Param("name") String name,
                                                    @Param("location") String location,
                                                    @Param("companyId") String companyId);
    
    @Query("SELECT c FROM ConferenceRoom c WHERE c.location = :location AND c.companyId = :companyId")
    Page<ConferenceRoom> findByLocationAndCompanyId(@Param("location") String location,
                                                   @Param("companyId") String companyId,
                                                   Pageable pageable);
    
    

}

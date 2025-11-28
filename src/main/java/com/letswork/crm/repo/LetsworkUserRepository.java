package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.LetsworkUser;





@Repository
public interface LetsworkUserRepository extends JpaRepository<LetsworkUser, Long> {

    
//    @Query("SELECT c FROM Client c WHERE c.email = :email AND c.clientCompany.name = :companyName")
//    Client findByEmailAndCompany(@Param("email") String email, @Param("companyName") String companyName);
//
//    
//    @Query("SELECT c FROM Client c WHERE c.ompany.name = :companyName")
//    List<Client> findAllByCompany(@Param("companyName") String companyName);
    
 
    List<LetsworkUser> findByFirstNameContainingIgnoreCase(String firstName);
    
    LetsworkUser findByEmailAndCompanyId(String email, String companyId);
    
    Page<LetsworkUser> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
    
    @Query("SELECT c FROM LetsworkUser c WHERE c.email = :email AND c.companyId = :companyId")
    LetsworkUser findByNameAndEmailAndCompanyId(@Param("email") String email,
                                         @Param("companyId") String companyId);
    
 
    @Query("SELECT c FROM LetsworkUser c WHERE c.companyIfApplicable = :companyName AND c.companyId = :companyId")
    List<LetsworkUser> findByClientCompanyNameAndCompanyId(@Param("companyName") String companyName,
                                                    @Param("companyId") String companyId);
    
    @Query("SELECT c FROM LetsworkUser c WHERE c.companyIfApplicable = :companyName AND c.companyId = :companyId")
    Page<LetsworkUser> findByClientCompanyNameAndCompanyId(
            @Param("companyName") String companyName,
            @Param("companyId") String companyId,
            Pageable pageable);

    
    @Query("SELECT c FROM LetsworkUser c WHERE c.companyId = :companyId")
    List<LetsworkUser> findIndividualClients(@Param("companyId") String companyId);
    
    @Query("SELECT c FROM LetsworkUser c WHERE c.companyId = :companyId")
    Page<LetsworkUser> findIndividualClients(@Param("companyId") String companyId, Pageable pageable);
    
    
    @Query("SELECT c FROM LetsworkUser c WHERE c.companyId = :companyId AND c.letsWorkCentre = :letsWorkCentre")
    List<LetsworkUser> findIndividualClientsByLetsWorkCentre(@Param("letsWorkCentre") String letsWorkCentre,
                                                @Param("companyId") String companyId);
    
    @Query("SELECT c FROM LetsworkUser c WHERE c.companyId = :companyId AND c.letsWorkCentre = :letsWorkCentre AND c.city = :city AND c.state = :state")
    Page<LetsworkUser> findIndividualClientsByLetsWorkCentre(
            @Param("letsWorkCentre") String letsWorkCentre,
            @Param("companyId") String companyId,
            @Param("city") String city,
            @Param("state") String state,
            Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM LetsworkUser c WHERE c.companyIfApplicable = :clientCompanyName AND c.companyId = :companyId")
    Long getCountOfClientsInClientCompany(@Param("clientCompanyName") String clientCompanyName,
                                                          @Param("companyId") String companyId);	
    
    @Query(
            "SELECT c FROM LetsworkUser c " +
            "WHERE c.companyId = :companyId " +
            "AND (:letsWorkCentre IS NULL OR c.letsWorkCentre = :letsWorkCentre) " +
            "AND (:city IS NULL OR c.city = :city) " +
            "AND (:state IS NULL OR c.state = :state) " +
            "AND (" +
            "    :search IS NULL " +
            "    OR c.firstName LIKE %:search% " +
            "    OR c.lastName LIKE %:search% " +
            "    OR c.email LIKE %:search% " +
            "    OR c.phone LIKE %:search% " +
            "    OR c.companyIfApplicable LIKE %:search% " +
            "    OR c.businessCategory LIKE %:search%" +
            ")"
    )
    	Page<LetsworkUser> searchClients(@Param("companyId") String companyId,
    	                           @Param("letsWorkCentre") String letsWorkCentre,
    	                           @Param("city") String city,
    	                           @Param("state") String state,
    	                           @Param("search") String search,
    	                           Pageable pageable);
    
}
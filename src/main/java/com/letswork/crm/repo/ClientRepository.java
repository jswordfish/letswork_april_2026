package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Client;





@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    
//    @Query("SELECT c FROM Client c WHERE c.email = :email AND c.clientCompany.name = :companyName")
//    Client findByEmailAndCompany(@Param("email") String email, @Param("companyName") String companyName);
//
//    
//    @Query("SELECT c FROM Client c WHERE c.ompany.name = :companyName")
//    List<Client> findAllByCompany(@Param("companyName") String companyName);
    
 
    List<Client> findByFirstNameContainingIgnoreCase(String firstName);
    
    Client findByEmailAndCompanyId(String email, String companyId);
    
    Page<Client> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
    
    @Query("SELECT c FROM Client c WHERE c.email = :email AND c.companyId = :companyId")
    Client findByNameAndEmailAndCompanyId(@Param("email") String email,
                                         @Param("companyId") String companyId);
    
 
    @Query("SELECT c FROM Client c WHERE c.clientCompanyName = :companyName AND c.companyId = :companyId")
    List<Client> findByClientCompanyNameAndCompanyId(@Param("companyName") String companyName,
                                                    @Param("companyId") String companyId);
    
    @Query("SELECT c FROM Client c WHERE c.clientCompanyName = :companyName AND c.companyId = :companyId")
    Page<Client> findByClientCompanyNameAndCompanyId(
            @Param("companyName") String companyName,
            @Param("companyId") String companyId,
            Pageable pageable);

    
    @Query("SELECT c FROM Client c WHERE c.companyId = :companyId")
    List<Client> findIndividualClients(@Param("companyId") String companyId);
    
    @Query("SELECT c FROM Client c WHERE c.companyId = :companyId")
    Page<Client> findIndividualClients(@Param("companyId") String companyId, Pageable pageable);
    
    
    @Query("SELECT c FROM Client c WHERE c.companyId = :companyId AND c.letsWorkCentre = :letsWorkCentre")
    List<Client> findIndividualClientsByLetsWorkCentre(@Param("letsWorkCentre") String letsWorkCentre,
                                                @Param("companyId") String companyId);
    
    @Query("SELECT c FROM Client c WHERE c.companyId = :companyId AND c.letsWorkCentre = :letsWorkCentre AND c.city = :city AND c.state = :state")
    Page<Client> findIndividualClientsByLetsWorkCentre(
            @Param("letsWorkCentre") String letsWorkCentre,
            @Param("companyId") String companyId,
            @Param("city") String city,
            @Param("state") String state,
            Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Client c WHERE c.clientCompanyName = :clientCompanyName AND c.companyId = :companyId")
    Long getCountOfClientsInClientCompany(@Param("clientCompanyName") String clientCompanyName,
                                                          @Param("companyId") String companyId);	
    
}
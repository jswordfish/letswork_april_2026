package com.LetsWork.CRM.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LetsWork.CRM.entities.Client;



@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    
//    @Query("SELECT c FROM Client c WHERE c.email = :email AND c.clientCompany.name = :companyName")
//    Client findByEmailAndCompany(@Param("email") String email, @Param("companyName") String companyName);
//
//    
//    @Query("SELECT c FROM Client c WHERE c.ompany.name = :companyName")
//    List<Client> findAllByCompany(@Param("companyName") String companyName);
    
 
    List<Client> findByNameContainingIgnoreCase(String name);
    
    Client findByEmailAndCompanyId(String email, String companyId);
    
    Page<Client> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT c FROM Client c WHERE c.name = :name AND c.email = :email AND c.companyId = :companyId")
    Client findByNameAndEmailAndCompanyId(@Param("name") String name,
                                         @Param("email") String email,
                                         @Param("companyId") String companyId);
    
 
    @Query("SELECT c FROM Client c WHERE c.clientCompanyName = :companyName AND c.companyId = :companyId")
    List<Client> findByClientCompanyNameAndCompanyId(@Param("companyName") String companyName,
                                                    @Param("companyId") String companyId);
    
    @Query("SELECT c FROM Client c WHERE c.clientCompanyName = :companyName AND c.companyId = :companyId")
    Page<Client> findByClientCompanyNameAndCompanyId(
            @Param("companyName") String companyName,
            @Param("companyId") String companyId,
            Pageable pageable);

    
    @Query("SELECT c FROM Client c WHERE c.isIndividual = true AND c.companyId = :companyId")
    List<Client> findIndividualClients(@Param("companyId") String companyId);
    
    @Query("SELECT c FROM Client c WHERE c.isIndividual = true AND c.companyId = :companyId")
    Page<Client> findIndividualClients(@Param("companyId") String companyId, Pageable pageable);
    
    
    @Query("SELECT c FROM Client c WHERE c.isIndividual = true AND c.companyId = :companyId AND c.location = :location")
    List<Client> findIndividualClientsByLocation(@Param("location") String location,
                                                @Param("companyId") String companyId);
    
    @Query("SELECT c FROM Client c WHERE c.isIndividual = true AND c.companyId = :companyId AND c.location = :location")
    Page<Client> findIndividualClientsByLocation(@Param("location") String location,
                                                @Param("companyId") String companyId,
                                                Pageable pageable);
    
}
package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ClientCompany;
import com.letswork.crm.entities.LetsWorkCentre;



@Repository
public interface ClientCompanyRepository extends JpaRepository<ClientCompany, Long> {

    
    List<ClientCompany> findByLetsWorkCentre(LetsWorkCentre letsWorkCentre);
    
    @Query("SELECT c FROM ClientCompany c WHERE c.clientCompanyName = :companyName AND c.companyId = :companyId")
    ClientCompany findByClientCompanyNameAndCompanyId(@Param("companyName") String companyName,
                                                          @Param("companyId") String companyId);	
    
    @Query("SELECT c FROM ClientCompany c WHERE c.letsWorkCentre = :letsWorkCentre AND c.companyId = :companyId")
    List<ClientCompany> findByLetsWorkCentreAndCompanyId(@Param("letsWorkCentre") String letsWorkCentre,
                                                  @Param("companyId") String companyId);
    
    @Query("SELECT c FROM ClientCompany c WHERE c.letsWorkCentre = :letsWorkCentre AND c.companyId = :companyId")
    Page<ClientCompany> findByLetsWorkCentreAndCompanyId(@Param("letsWorkCentre") String letsWorkCentre,
                                                  @Param("companyId") String companyId,
                                                  Pageable pageable);
    
   
    
}

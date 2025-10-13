package com.LetsWork.CRM.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LetsWork.CRM.entities.ClientCompany;
import com.LetsWork.CRM.entities.Location;



@Repository
public interface ClientCompanyRepository extends JpaRepository<ClientCompany, Long> {

    
    ClientCompany findByClientCompanyName(String companyName);

    
    List<ClientCompany> findByIndustry(String industry);
    
    
    List<ClientCompany> findAll();
    
    Page<ClientCompany> findAll(Pageable pageable);
    
    
    List<ClientCompany> findByLocation(Location location);
    
    @Query("SELECT c FROM ClientCompany c WHERE c.clientCompanyName = :companyName AND c.location = :location AND c.companyId = :companyId")
    ClientCompany findByClientCompanyNameAndLocationAndCompanyId(@Param("companyName") String companyName,
                                                          @Param("location") String location,
                                                          @Param("companyId") String companyId);	
    
    @Query("SELECT c FROM ClientCompany c WHERE c.location = :location AND c.companyId = :companyId")
    List<ClientCompany> findByLocationAndCompanyId(@Param("location") String location,
                                                  @Param("companyId") String companyId);
    
    @Query("SELECT c FROM ClientCompany c WHERE c.location = :location AND c.companyId = :companyId")
    Page<ClientCompany> findByLocationAndCompanyId(@Param("location") String location,
                                                  @Param("companyId") String companyId,
                                                  Pageable pageable);
    
}

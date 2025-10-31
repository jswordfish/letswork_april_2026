package com.letswork.crm.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ClientCompanySeatMapping;

@Repository
public interface ClientCompanySeatMappingRepository extends JpaRepository<ClientCompanySeatMapping, Long> {

    @Query("SELECT c FROM ClientCompanySeatMapping c WHERE c.clientCompanyName = :clientCompanyName " +
           "AND c.letsWorkCentre = :letsWorkCentre AND c.companyId = :companyId " +
           "AND c.city = :city AND c.state = :state")
    Page<ClientCompanySeatMapping> findByClientCompanyNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
            @Param("clientCompanyName") String clientCompanyName,
            @Param("letsWorkCentre") String letsWorkCentre,
            @Param("companyId") String companyId,
            @Param("city") String city,
            @Param("state") String state,
            Pageable pageable);

    @Query("SELECT c FROM ClientCompanySeatMapping c WHERE c.letsWorkCentre = :letsWorkCentre " +
           "AND c.companyId = :companyId AND c.city = :city AND c.state = :state")
    Page<ClientCompanySeatMapping> findByLetsWorkCentreAndCompanyIdAndCityAndState(
            @Param("letsWorkCentre") String letsWorkCentre,
            @Param("companyId") String companyId,
            @Param("city") String city,
            @Param("state") String state,
            Pageable pageable);

    @Query("SELECT c FROM ClientCompanySeatMapping c WHERE c.companyId = :companyId")
    Page<ClientCompanySeatMapping> findByCompanyId(@Param("companyId") String companyId, Pageable pageable);
    
}

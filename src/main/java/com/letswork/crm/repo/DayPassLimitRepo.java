package com.letswork.crm.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.DayPassLimit;

@Repository
public interface DayPassLimitRepo extends JpaRepository<DayPassLimit, Long> {

    DayPassLimit findByLetsWorkCentreAndCompanyIdAndCityAndState(
            String letsWorkCentre,
            String companyId,
            String city,
            String state
    );
    
    

    Page<DayPassLimit> findAllByCompanyId(String companyId, Pageable pageable);

    Page<DayPassLimit> findAllByCompanyIdAndLetsWorkCentreAndCityAndState(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            Pageable pageable
    );
    
}

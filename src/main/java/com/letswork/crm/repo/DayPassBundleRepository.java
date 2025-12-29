package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.DayPassBundle;

@Repository
public interface DayPassBundleRepository
        extends JpaRepository<DayPassBundle, Long> {

    DayPassBundle findByLetsWorkCentreAndCompanyIdAndCityAndState(
            String letsWorkCentre,
            String companyId,
            String city,
            String state
    );

    List<DayPassBundle> findAllByCompanyId(String companyId);
}

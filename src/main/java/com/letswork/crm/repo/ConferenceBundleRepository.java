package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceBundle;

@Repository
public interface ConferenceBundleRepository
        extends JpaRepository<ConferenceBundle, Long> {

    ConferenceBundle findByNumberOfHoursAndCompanyId(
            Integer numberOfHours,
            String companyId
    );

    List<ConferenceBundle> findAllByCompanyId(String companyId);
    
}

package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.OffersToCentreMapping;

@Repository
public interface OffersToCentreMappingRepository
        extends JpaRepository<OffersToCentreMapping, Long> {

    List<OffersToCentreMapping> findByOfferNameAndCompanyId(String offerName, String companyId);

    void deleteByOfferNameAndCompanyId(String offerName, String companyId);
        
}

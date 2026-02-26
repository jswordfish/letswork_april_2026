package com.letswork.crm.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Offers;
import com.letswork.crm.enums.OfferType;

@Repository
public interface OffersRepository extends JpaRepository<Offers, Long> {

    Optional<Offers> findByNameAndCompanyId(String name, String companyId);

    Optional<Offers> findByCodeAndCompanyId(String code, String companyId);

    List<Offers> findByCompanyId(String companyId);
    
    List<Offers> findAllByCompanyId(String companyId);
    
    List<Offers> findAllByCompanyIdAndOfferType(String companyId, OfferType offerType);
    
}

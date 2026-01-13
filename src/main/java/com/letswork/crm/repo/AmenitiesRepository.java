package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Amenities;
import com.letswork.crm.enums.AmenityType;

@Repository
public interface AmenitiesRepository extends JpaRepository<Amenities, Long> {

    Amenities findByNameAndCompanyId(String name, String companyId);

    Page<Amenities> findByAmenityTypeAndCompanyId(
            AmenityType amenityType,
            String companyId,
            Pageable pageable
    );

    Page<Amenities> findByCompanyId(
            String companyId,
            Pageable pageable
    );

    List<Amenities> findByAmenityTypeAndCompanyId(AmenityType amenityType, String companyId);
    List<Amenities> findByCompanyId(String companyId);
    
}

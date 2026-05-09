package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Amenities;
import com.letswork.crm.enums.AmenityType;

@Repository
public interface AmenitiesRepository extends JpaRepository<Amenities, Long> {

    Amenities findByNameAndCompanyId(String name, String companyId);
    
    Amenities findByNameAndCompanyIdAndAmenityType(String name, String companyId, AmenityType amenityType);

    Page<Amenities> findByAmenityTypeAndCompanyId(
            AmenityType amenityType,
            String companyId,
            Pageable pageable
    );
    
    @Query("SELECT a FROM Amenities a " +
    	       "WHERE (:companyId IS NULL OR a.companyId = :companyId) " +
    	       "AND (:type IS NULL OR a.amenityType = :type) " +
    	       "AND (" +
    	       "   :search IS NULL " +
    	       "   OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "   OR LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "   OR LOWER(a.amenityType) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "   OR LOWER(a.s3Path) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       ")")
    	Page<Amenities> searchAmenities(
    	        @Param("companyId") String companyId,
    	        @Param("type") AmenityType type,
    	        @Param("search") String search,
    	        Pageable pageable
    	);

    Page<Amenities> findByCompanyId(
            String companyId,
            Pageable pageable
    );

    List<Amenities> findByAmenityTypeAndCompanyId(AmenityType amenityType, String companyId);
    List<Amenities> findByCompanyId(String companyId);
    
}

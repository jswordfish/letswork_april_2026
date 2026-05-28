package com.letswork.crm.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceBundle;

@Repository
public interface ConferenceBundleRepository
        extends JpaRepository<ConferenceBundle, Long> {

    ConferenceBundle findByNameAndNumberOfHoursAndCompanyId(
    		String name,
            Float numberOfHours,
            String companyId
    );

    List<ConferenceBundle> findAllByCompanyId(String companyId);
    
    @Query("SELECT cb FROM ConferenceBundle cb " +
    	       "WHERE cb.companyId = :companyId " +

    	       "AND ( " +
    	       "   :showInApp IS NULL " +
    	       "   OR cb.showInApp = :showInApp " +
    	       ") " +

    	       "AND ( " +
    	       "   :fromDate IS NULL " +
    	       "   OR cb.createDate >= :fromDate " +
    	       ") " +

    	       "AND ( " +
    	       "   :toDate IS NULL " +
    	       "   OR cb.createDate <= :toDate " +
    	       ") " +

    	       "AND ( " +
    	       "   :search IS NULL " +
    	       "   OR LOWER(cb.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "   OR LOWER(CAST(cb.numberOfHours as string)) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "   OR LOWER(CAST(cb.price as string)) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "   OR LOWER(CAST(cb.validForDays as string)) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "   OR LOWER(CAST(cb.showInApp as string)) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       ")")
    	Page<ConferenceBundle> filter(
    	        @Param("companyId") String companyId,
    	        @Param("showInApp") Boolean showInApp,
    	        @Param("search") String search,
    	        @Param("fromDate") LocalDateTime fromDate,
    	        @Param("toDate") LocalDateTime toDate,
    	        Pageable pageable
    	);
    
}

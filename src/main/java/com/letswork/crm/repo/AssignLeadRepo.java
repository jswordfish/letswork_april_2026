package com.letswork.crm.repo;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.AssignLead;

@Repository
public interface AssignLeadRepo
        extends JpaRepository<AssignLead, Long> {

    Optional<AssignLead> findByIdAndCompanyId(
            Long id,
            String companyId
    );

    Optional<AssignLead> findByLeadIdAndCompanyId(
            Long leadId,
            String companyId
    );
    
    Optional<AssignLead> findFirstByLeadId(Long leadId);
    
    AssignLead findByLeadId(Long leadId);
    
    List<AssignLead> findByLeadIdIn(Collection<Long> leadIds);
    
    @Query(
    	    "SELECT a FROM AssignLead a " +
    	    "WHERE a.companyId = :companyId " +
    	    "AND (:leadId IS NULL OR a.leadId = :leadId) " +
    	    "AND (:userId IS NULL OR a.userId = :userId) " +
    	    "AND (:fromDate IS NULL OR a.createDate >= :fromDate) " +
    	    "AND (:toDate IS NULL OR a.createDate <= :toDate) " +
    	    "AND (" +
    	        ":search IS NULL OR " +
    	        "CAST(a.leadId AS string) LIKE CONCAT('%', :search, '%') OR " +
    	        "CAST(a.userId AS string) LIKE CONCAT('%', :search, '%')" +
    	    ")"
    	)
    	Page<AssignLead> filter(
    	        @Param("companyId") String companyId,
    	        @Param("leadId") Long leadId,
    	        @Param("userId") Long userId,
    	        @Param("search") String search,
    	        @Param("fromDate") Date fromDate,
    	        @Param("toDate") Date toDate,
    	        Pageable pageable
    	);
    
}

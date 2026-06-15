package com.letswork.crm.repo;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Activity;
import com.letswork.crm.enums.ActionType;

@Repository
public interface ActivityRepo extends JpaRepository<Activity, Long> {
	
	Activity findByIdAndCompanyId(
	        Long id,
	        String companyId
	);
	
	@Query(
		    "SELECT a FROM Activity a " +
		    "WHERE a.companyId = :companyId " +
		    "AND (:leadId IS NULL OR a.leadId = :leadId) " +
		    "AND (:header IS NULL OR a.header = :header) " +
		    "AND (:actionType IS NULL OR a.actionType = :actionType) " +
		    "AND (:fromDate IS NULL OR a.createDate >= :fromDate) " +
		    "AND (:toDate IS NULL OR a.createDate <= :toDate) " +
		    "AND (" +
		        ":search IS NULL OR " +
		        "LOWER(a.header) LIKE LOWER(CONCAT('%', :search, '%'))" +
		    ")"
		)
		Page<Activity> filter(
		        @Param("companyId") String companyId,
		        @Param("leadId") Long leadId,
		        @Param("header") String header,
		        @Param("actionType") ActionType actionType,
		        @Param("search") String search,
		        @Param("fromDate") Date fromDate,
		        @Param("toDate") Date toDate,
		        Pageable pageable
		);
	
}

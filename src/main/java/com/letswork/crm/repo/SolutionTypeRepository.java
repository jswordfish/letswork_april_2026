package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.SolutionType;

@Repository
public interface SolutionTypeRepository extends JpaRepository<SolutionType, Long> {

    SolutionType findByNameAndCompanyId(String name, String companyId);

    List<SolutionType> findAllByCompanyId(String companyId);
    
    @Query("SELECT s FROM SolutionType s " +
    	       "WHERE s.companyId = :companyId " +
    	       "AND (" +
    	       "    :search IS NULL " +
    	       "    OR CAST(s.id AS string) LIKE CONCAT('%', CONCAT(:search, '%')) " +
    	       "    OR LOWER(s.name) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
    	       ")")
    	List<SolutionType> search(
    	        @Param("companyId") String companyId, 
    	        @Param("search") String search
    	);
    
}

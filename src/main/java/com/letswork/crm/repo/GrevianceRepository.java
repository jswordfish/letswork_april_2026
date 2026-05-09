package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Greviance;
import com.letswork.crm.enums.GrevianceStatus;

@Repository
public interface GrevianceRepository extends JpaRepository<Greviance, Long> {

	@Query("SELECT g FROM Greviance g " +
		       "WHERE g.companyId = :companyId " +
		       "AND (:clientId IS NULL OR g.clientId = :clientId) " +
		       "AND (:centre IS NULL OR g.letsWorkCentre = :centre) " +
		       "AND (:city IS NULL OR g.city = :city) " +
		       "AND (:state IS NULL OR g.state = :state) " +
		       "AND (:category IS NULL OR g.category = :category) " +
		       "AND (:subCategory IS NULL OR g.subCategory = :subCategory) " +
		       "AND (:status IS NULL OR g.grevianceStatus = :status) " +
		       "AND (" +
		       "    :search IS NULL " +
		       "    OR CAST(g.clientId AS string) LIKE CONCAT('%', CONCAT(:search, '%')) " +
		       "    OR LOWER(g.letsWorkCentre) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(g.city) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(g.state) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(g.category) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(g.subCategory) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(g.issue) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR CAST(g.grevianceStatus AS string) LIKE CONCAT('%', CONCAT(:search, '%')) " +
		       "    OR LOWER(g.imageS3Key) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       ")")
		Page<Greviance> filter(
		        @Param("companyId") String companyId,
		        @Param("clientId") Long clientId,
		        @Param("centre") String centre,
		        @Param("city") String city,
		        @Param("state") String state,
		        @Param("category") String category,
		        @Param("subCategory") String subCategory,
		        @Param("status") GrevianceStatus status,
		        @Param("search") String search,
		        Pageable pageable
		);
    
    Optional<Greviance> findByIdAndCompanyId(Long id, String companyId);
    
}

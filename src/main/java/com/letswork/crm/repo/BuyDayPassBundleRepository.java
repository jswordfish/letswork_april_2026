package com.letswork.crm.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.dtos.CentreDayPassSummaryDto;
import com.letswork.crm.entities.BuyDayPassBundle;

@Repository
public interface BuyDayPassBundleRepository extends JpaRepository<BuyDayPassBundle, Long> {
	
	List<BuyDayPassBundle> findByCompanyId(String companyId);
	
	List<BuyDayPassBundle> findByEmailAndCompanyId(String email, String companyId);
	
	List<BuyDayPassBundle> findByBundleIdAndCompanyId(Long bundleId, String companyId);
	
	@Query(
		    "SELECT b FROM BuyDayPassBundle b " +
		    "WHERE b.companyId = :companyId " +
		    "AND (:email IS NULL OR b.email = :email) " +
		    "AND (:bundleId IS NULL OR b.bundleId = :bundleId) " +
		    "AND (:centre IS NULL OR b.letsWorkCentre = :centre) " +
		    "AND (:city IS NULL OR b.city = :city) " +
		    "AND (:state IS NULL OR b.state = :state) " +
		    "AND (:fromDate IS NULL OR b.purchaseDate >= :fromDate) " +
		    "AND (:toDate IS NULL OR b.purchaseDate <= :toDate)"
		)
		Page<BuyDayPassBundle> findByFilters(
		        @Param("companyId") String companyId,
		        @Param("email") String email,
		        @Param("bundleId") Long bundleId,
		        @Param("centre") String letsWorkCentre,
		        @Param("city") String city,
		        @Param("state") String state,
		        @Param("fromDate") LocalDateTime fromDate,
		        @Param("toDate") LocalDateTime toDate,
		        Pageable pageable
		);
	
	@Query(
	        "SELECT new com.letswork.crm.dtos.CentreDayPassSummaryDto(" +
	        "   b.letsWorkCentre, " +
	        "   b.city, " +
	        "   b.state, " +
	        "   SUM(CAST(b.numberOfDays AS integer))" +
	        ") " +
	        "FROM BuyDayPassBundle b " +
	        "WHERE b.companyId = :companyId " +
	        "  AND (:email IS NULL OR b.email = :email) " +
	        "  AND (:letsWorkCentre IS NULL OR b.letsWorkCentre = :letsWorkCentre) " +
	        "  AND (:city IS NULL OR b.city = :city) " +
	        "  AND (:state IS NULL OR b.state = :state) " +
	        "GROUP BY b.letsWorkCentre, b.city, b.state"
	    )
	    List<CentreDayPassSummaryDto> getCentreWiseSummary(
	            @Param("companyId") String companyId,
	            @Param("email") String email,
	            @Param("letsWorkCentre") String letsWorkCentre,
	            @Param("city") String city,
	            @Param("state") String state
	    );
	
	@Query("SELECT b FROM BuyDayPassBundle b " +
		       "WHERE b.email = :email " +
		       "AND b.companyId = :companyId " +
		       "AND b.letsWorkCentre = :centre " +
		       "AND b.expiryDate > :now " +
		       "ORDER BY b.expiryDate ASC")
		List<BuyDayPassBundle> findActiveBundles(
		        @Param("email") String email,
		        @Param("companyId") String companyId,
		        @Param("centre") String centre,
		        @Param("now") LocalDateTime now
		);

}

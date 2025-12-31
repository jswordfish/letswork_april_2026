package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
	        "AND (:state IS NULL OR b.state = :state)"
	    )
	    List<BuyDayPassBundle> findByFilters(
	            @Param("companyId") String companyId,
	            @Param("email") String email,
	            @Param("bundleId") Long bundleId,
	            @Param("centre") String letsWorkCentre,
	            @Param("city") String city,
	            @Param("state") String state
	    );

}

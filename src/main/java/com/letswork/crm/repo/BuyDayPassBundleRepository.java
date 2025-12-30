package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.BuyDayPassBundle;

@Repository
public interface BuyDayPassBundleRepository extends JpaRepository<BuyDayPassBundle, Long> {
	
	List<BuyDayPassBundle> findByCompanyId(String companyId);
	
	List<BuyDayPassBundle> findByEmailAndCompanyId(String email, String companyId);
	
	List<BuyDayPassBundle> findByBundleIdAndCompanyId(Long bundleId, String companyId);

}

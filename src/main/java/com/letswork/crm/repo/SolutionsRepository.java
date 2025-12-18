package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Solutions;

@Repository
public interface SolutionsRepository extends JpaRepository<Solutions, Long>{
	
	Solutions findByNameAndLetsWorkCentreAndCompanyId(String name, String letsWorkCentre, String companyId);
	
	List<Solutions> findByLetsWorkCentreAndCompanyId(String letsWorkCentre, String companyId);
	
	List<Solutions> findByCompanyId(String companyId);

}

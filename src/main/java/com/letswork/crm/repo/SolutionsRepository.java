package com.letswork.crm.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Solutions;

@Repository
public interface SolutionsRepository extends JpaRepository<Solutions, Long>{
	
	Solutions findByNameAndLetsWorkCentreAndCompanyId(String name, String letsWorkCentre, String companyId);

}

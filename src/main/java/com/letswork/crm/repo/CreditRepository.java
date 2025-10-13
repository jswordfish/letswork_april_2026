package com.letswork.crm.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Credit;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long>{
	
	Page<Credit> findByCompanyId(String companyId, Pageable pageable);

}

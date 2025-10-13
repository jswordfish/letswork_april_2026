package com.letswork.crm.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.UserCredit;

@Repository
public interface UserCreditRepository extends JpaRepository<UserCredit, Long>{
	
	UserCredit findByUserEmailAndCompanyId(String userEmail, String companyId);
	
	Page<UserCredit> findByCompanyId(String companyId, Pageable pageable);

}

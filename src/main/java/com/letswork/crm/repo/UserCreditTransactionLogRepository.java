package com.LetsWork.CRM.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LetsWork.CRM.entities.UserCreditTransactionLog;

@Repository
public interface UserCreditTransactionLogRepository extends JpaRepository<UserCreditTransactionLog, Long>{
	
	Page<UserCreditTransactionLog> findByUserEmailAndCompanyId(String userEmail, String companyId, Pageable pageable);
	
	Page<UserCreditTransactionLog> findByCompanyId(String companyId, Pageable pageable);

}

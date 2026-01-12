package com.letswork.crm.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.NewUserRegister;

@Repository
public interface NewUserRegisterRepository extends JpaRepository<NewUserRegister, Long> {

	Optional<NewUserRegister>
	findByEmailAndCompanyId(String email, String companyId);

	Optional<NewUserRegister>
	findByPhoneNumberAndCompanyId(String phoneNumber, String companyId);

    List<NewUserRegister> findByCompanyId(String companyId);
    
    List<NewUserRegister> findByMonthlyTrueAndCompanyId(
            String companyId
    );
    
}
package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.NewUserRegister;

@Repository
public interface NewUserRegisterRepository extends JpaRepository<NewUserRegister, Long> {

    NewUserRegister findByEmailAndCompanyId(
            String email,
            String companyId);

    List<NewUserRegister> findByCompanyId(String companyId);
    
}
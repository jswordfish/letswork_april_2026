package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.NewUserRegister;

public interface NewUserRegisterService {
	
	NewUserRegister saveOrUpdate(NewUserRegister user);

    List<NewUserRegister> getAllByCompanyId(String companyId);

    NewUserRegister getByEmailAndCompanyId(
            String email,
            String companyId);

}

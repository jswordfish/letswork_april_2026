package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.NewUserRegister;

public interface NewUserRegisterService {
	
	public NewUserRegister save(NewUserRegister user);

    List<NewUserRegister> getAllByCompanyId(String companyId);

    NewUserRegister getByEmailAndCompanyId(
            String email,
            String companyId);

}

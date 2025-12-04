package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.Roles;

public interface RolesService {
	
	Roles saveOrUpdate(Roles role);

    List<Roles> listByCompanyId(String companyId);

}

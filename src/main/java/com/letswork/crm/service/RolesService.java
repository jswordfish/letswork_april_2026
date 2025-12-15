package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.dtos.RbacRoleResponseDTO;
import com.letswork.crm.entities.Rbac_entity;

public interface RolesService {
	
	Rbac_entity saveOrUpdate(Rbac_entity role);

	List<RbacRoleResponseDTO> listGroupedByCompany(String companyId);
    
    List<Rbac_entity> listByRole(String role, String companyId);
    
    RbacRoleResponseDTO getRoleGrouped(String role, String companyId);

}

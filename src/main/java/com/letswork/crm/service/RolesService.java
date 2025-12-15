package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.dtos.RbacRoleResponseDTO;
import com.letswork.crm.entities.Rbac_entity;

public interface RolesService {
	
	RbacRoleResponseDTO saveOrUpdateGrouped(RbacRoleResponseDTO dto, String companyId);

	List<RbacRoleResponseDTO> listGroupedByCompany(String companyId);
    
    List<Rbac_entity> listByRole(String role, String companyId);
    
    RbacRoleResponseDTO getRoleGrouped(String role, String companyId);

}

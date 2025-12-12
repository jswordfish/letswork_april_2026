package com.letswork.crm.dtos;

import java.util.Map;

import lombok.Data;

@Data
public class RbacRoleResponseDTO {
	
	private String name;
    private Map<String, MenuPermissionDTO> menu_items;

}

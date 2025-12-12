package com.letswork.crm.dtos;

import lombok.Data;

@Data
public class MenuPermissionDTO {
	
	private Boolean page_create;
    private Boolean page_edit;
    private Boolean page_delete;
    private Boolean page_view;

}

package com.letswork.crm.dtos;

import java.util.List;

import lombok.Data;

@Data
public class BulkAssignLeadRequest {
	
	private String companyId;

    private Long userId;

    private List<Long> leadIds;

}

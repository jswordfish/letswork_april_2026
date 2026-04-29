package com.letswork.crm.dtos;

import lombok.Data;

@Data
public class SolutionsDto {
	
	private Long id;
    private String name;
    private String price;
    private String letsWorkCentre;
    private String city;
    private String state;
    private String amenities;

    private Long solutionTypeId;

    private String companyId;

}

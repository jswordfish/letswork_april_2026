package com.letswork.crm.dtos;

import java.util.Date;

import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.enums.GrevianceStatus;

import lombok.Data;

@Data
public class GrevianceResponseDto {
	
	private Long id;
    private String companyId;
    private Long clientId;

    private LetsWorkClient client; 

    private String letsWorkCentre;
    private String city;
    private String state;

    private String category;
    private String subCategory;

    private GrevianceStatus grevianceStatus;

    private String description;

    private Date createDate;

}

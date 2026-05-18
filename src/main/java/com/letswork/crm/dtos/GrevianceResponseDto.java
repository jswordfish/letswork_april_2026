package com.letswork.crm.dtos;

import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.enums.GrevianceStatus;

import lombok.Data;

@Data
public class GrevianceResponseDto {
	
	private Long id;

    private Long clientId;
    private Long userId;

    private LetsWorkClient client;
    private NewUserRegister user;

    private String letsWorkCentre;
    private String city;
    private String state;
    private String category;
    private String subCategory;
    private String issue;
    private GrevianceStatus grevianceStatus;
    private String imageS3Key;

}

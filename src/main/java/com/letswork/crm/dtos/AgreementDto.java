package com.letswork.crm.dtos;

import com.letswork.crm.entities.Contract;

import lombok.Data;

@Data
public class AgreementDto extends Contract{
	
	private String leadName;
	
	private String leadEmail;
	
	private String leadCompanyName;

}

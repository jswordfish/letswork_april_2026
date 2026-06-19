package com.letswork.crm.dtos;

import com.letswork.crm.entities.Contract;
import com.letswork.crm.entities.NewUserRegister;

import lombok.Data;

@Data
public class ConvertedContractDto {
	
	private Contract contract;
	
	private NewUserRegister newUserRegister;

}

package com.letswork.crm.dtos;

import java.util.List;

import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.NewUserRegister;

import lombok.Data;

@Data
public class UserWithCompaniesDto {
	
	private NewUserRegister user;
    private List<LetsWorkClient> companies;

    public UserWithCompaniesDto(
            NewUserRegister user,
            List<LetsWorkClient> companies
    ) {
        this.user = user;
        this.companies = companies;
    }

}

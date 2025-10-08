package com.LetsWork.CRM.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Tenant extends Base{
	
	String orgName;
	
	@Column(length = 500)
	String address;
	
	
	String tenantAdminEmail;
	
	String tenantAdminPhone;
	
	String password = "12345";
	
	
}


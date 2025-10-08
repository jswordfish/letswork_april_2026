package com.LetsWork.CRM.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.LetsWork.CRM.enums.CreditTransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreditTransactionLog extends Base {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	String userEmail;
	
	int totalCredits;
	
	CreditTransactionType creditTransactionType;
	
	String creditsUsedOn;
	
	String companyId;

}

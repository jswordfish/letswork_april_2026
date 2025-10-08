package com.LetsWork.CRM.entities;

import com.LetsWork.CRM.enums.CreditTransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

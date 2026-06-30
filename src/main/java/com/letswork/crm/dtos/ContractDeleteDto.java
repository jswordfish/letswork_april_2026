package com.letswork.crm.dtos;

import java.time.LocalDate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.letswork.crm.enums.CancelContractType;

import lombok.Data;

@Data
public class ContractDeleteDto {
	
	private Long contractId;
	
	private String cancelDescription;
	
	@Enumerated(EnumType.STRING)
	private CancelContractType cancelContractType;
	
	private LocalDate noticePeriodStartDate;
	
	private LocalDate actualEndDate;

}

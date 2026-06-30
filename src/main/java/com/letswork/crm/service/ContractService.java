package com.letswork.crm.service;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.AgreementDto;
import com.letswork.crm.dtos.ContractDeleteDto;
import com.letswork.crm.dtos.ConvertedContractDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.enums.ContractStatus;
import com.letswork.crm.enums.DateFilterType;

public interface ContractService {
	
	Contract saveOrUpdate(Contract contract);
	
	ConvertedContractDto saveOrUpdateConverted(
	        ConvertedContractDto dto,
	        MultipartFile agreement
	);
	
	public String sendAgreementDefaultOnMail(AgreementDto dto);
	
	String sendAgreementCustomOnMail(
	        String name,
	        String email,
	        MultipartFile pdf
	);

	PaginatedResponseDto getPaginated(
	        String companyId,
	        Long letsWorkClientId,
	        ContractStatus status,
	        LocalDate fromDate,
	        LocalDate toDate,
	        DateFilterType dateFilterType,
	        int page,
	        int size
	);
	
    Contract cancelContract(ContractDeleteDto dto);

}

package com.letswork.crm.service;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.AgreementDto;
import com.letswork.crm.dtos.ConvertedContractDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.enums.ContractStatus;

public interface ContractService {
	
	Contract saveOrUpdate(Contract contract);
	
	public ConvertedContractDto saveOrUpdateConverted(ConvertedContractDto dto);
	
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
            int page,
            int size
    );

}

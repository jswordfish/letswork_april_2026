package com.letswork.crm.service;

import java.time.LocalDate;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.enums.ContractStatus;

public interface ContractService {
	
	Contract saveOrUpdate(Contract contract);

    PaginatedResponseDto getPaginated(
            String companyId,
            Long letsWorkClientId,
            ContractStatus status,
            int page,
            int size
    );

}

package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.dtos.BulkSeatAssignmentRequestContract;
import com.letswork.crm.entities.ContractSeatMapping;

public interface ContractSeatMappingService {
	
	ContractSeatMapping saveOrUpdate(ContractSeatMapping mapping);

    List<ContractSeatMapping> assignMultipleSeatsToContract(BulkSeatAssignmentRequestContract request);

    List<ContractSeatMapping> getSeatsByContract(Long contractId, String companyId);

}

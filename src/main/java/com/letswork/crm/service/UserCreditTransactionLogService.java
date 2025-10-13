package com.letswork.crm.service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.UserCreditTransactionLog;
import com.letswork.crm.util.InsufficientCreditsException;

public interface UserCreditTransactionLogService {
	
	UserCreditTransactionLog logAndProcessTransaction(UserCreditTransactionLog transactionLog) throws InsufficientCreditsException;
    
    PaginatedResponseDto listUserTransactions(String userEmail, String companyId, int page, int size);
  
    PaginatedResponseDto listAllTransactions(String companyId, int page, int size);

}

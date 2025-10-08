package com.LetsWork.CRM.service;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.UserCreditTransactionLog;
import com.LetsWork.CRM.util.InsufficientCreditsException;

public interface UserCreditTransactionLogService {
	
	UserCreditTransactionLog logAndProcessTransaction(UserCreditTransactionLog transactionLog) throws InsufficientCreditsException;
    
    PaginatedResponseDto listUserTransactions(String userEmail, String companyId, int page, int size);
  
    PaginatedResponseDto listAllTransactions(String companyId, int page, int size);

}

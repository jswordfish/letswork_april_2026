package com.letswork.crm.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.User;
import com.letswork.crm.entities.UserCredit;
import com.letswork.crm.entities.UserCreditTransactionLog;
import com.letswork.crm.enums.CreditTransactionType;
import com.letswork.crm.repo.UserCreditTransactionLogRepository;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.service.UserCreditService;
import com.letswork.crm.service.UserCreditTransactionLogService;
import com.letswork.crm.util.InsufficientCreditsException;

@Service
@Transactional
public class UserCreditTransactionLogServiceImpl implements UserCreditTransactionLogService{
	
	@Autowired
    private UserCreditTransactionLogRepository logRepository;
	
    @Autowired
    private UserCreditService userCreditService; 
    
    @Autowired
    UserRepo userRepo;

    @Override
    @Transactional
    public UserCreditTransactionLog logAndProcessTransaction(
            UserCreditTransactionLog transactionLog) throws InsufficientCreditsException {
    	
		User user = userRepo.findByEmail(transactionLog.getUserEmail(), transactionLog.getCompanyId());
		
		if(user==null) {
			throw new RuntimeException("User does not exists");
		}

        // Assuming companyId, userEmail, totalCredits, and type are set on transactionLog
        String companyId = transactionLog.getCompanyId(); 
        String userEmail = transactionLog.getUserEmail();
        int transactionAmount = transactionLog.getTotalCredits();
        
        // 1. Get current credits
        UserCredit userCredit = userCreditService.getByUserEmail(userEmail, companyId);
        int currentCredits = (userCredit != null) ? userCredit.getCreditsLeft() : 0;
        
        int changeAmount = 0;

        if (transactionLog.getCreditTransactionType() == CreditTransactionType.debit) {
            // Business Logic: Check for insufficient credits
            if (currentCredits < transactionAmount) {
                throw new InsufficientCreditsException("failure : Not enough credits. User " + userEmail + " has only " + currentCredits + " credits, but tried to debit " + transactionAmount);
            }
            changeAmount = -transactionAmount; 
        } else {
            // Credit
            changeAmount = transactionAmount; 
        }
        
        
        UserCreditTransactionLog savedLog = logRepository.save(transactionLog);
        
        
        userCreditService.updateCredits(userEmail, companyId, changeAmount);
        
        return savedLog;
    }

    @Override
    public PaginatedResponseDto listUserTransactions(String userEmail, String companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserCreditTransactionLog> logPage = logRepository.findByUserEmailAndCompanyId(userEmail, companyId, pageable);
        
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setList(logPage.getContent());
        response.setSelectedPage(logPage.getNumber());
        response.setTotalNumberOfRecords((int) logPage.getTotalElements());
        response.setTotalNumberOfPages(logPage.getTotalPages());
        response.setRecordsFrom(page * size);
        response.setRecordsTo(Math.min((page * size) + size, (int) logPage.getTotalElements()));
        
        return response;
    }
    
    @Override
    public PaginatedResponseDto listAllTransactions(String companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserCreditTransactionLog> logPage = logRepository.findByCompanyId(companyId, pageable);
        
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setList(logPage.getContent());
        response.setSelectedPage(logPage.getNumber());
        response.setTotalNumberOfRecords((int) logPage.getTotalElements());
        response.setTotalNumberOfPages(logPage.getTotalPages());
        response.setRecordsFrom(page * size);
        response.setRecordsTo(Math.min((page * size) + size, (int) logPage.getTotalElements()));
        
        return response;
    }
}
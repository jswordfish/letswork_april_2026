package com.LetsWork.CRM.serviceImpl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.User;
import com.LetsWork.CRM.entities.UserCredit;
import com.LetsWork.CRM.repo.UserCreditRepository;
import com.LetsWork.CRM.repo.UserRepo;
import com.LetsWork.CRM.service.UserCreditService;

@Service
@Transactional
public class UserCreditServiceImpl implements UserCreditService{
	
	@Autowired
    private UserCreditRepository userCreditRepository;
	
	@Autowired
	UserRepo userRepo;
	
	@Override
    public UserCredit saveOrUpdate(UserCredit userCredit) {
		
		User user = userRepo.findByEmail(userCredit.getUserEmail(), userCredit.getCompanyId());
		
		if(user==null) {
			throw new RuntimeException("User does not exists");
		}
        
        UserCredit existingCredit = userCreditRepository.findByUserEmailAndCompanyId(
            userCredit.getUserEmail(), 
            userCredit.getCompanyId()
        );
        
        if (existingCredit != null) {
            // Update existing record attributes
            existingCredit.setCreditsLeft(userCredit.getCreditsLeft());
            return userCreditRepository.save(existingCredit);
        }
        
        // If not found, save as new
        return userCreditRepository.save(userCredit);
    }
    
    // ... listAll and deleteById implementations are similar to CreditServiceImpl ...
    @Override
    public PaginatedResponseDto listAll(String companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserCredit> creditsPage = userCreditRepository.findByCompanyId(companyId, pageable);
        
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setList(creditsPage.getContent());
        response.setSelectedPage(creditsPage.getNumber());
        response.setTotalNumberOfRecords((int) creditsPage.getTotalElements());
        response.setTotalNumberOfPages(creditsPage.getTotalPages());
        response.setRecordsFrom(page * size);
        response.setRecordsTo(Math.min((page * size) + size, (int) creditsPage.getTotalElements()));
        
        return response;
    }

    @Override
    @Transactional
    public UserCredit updateCredits(String userEmail, String companyId, int changeAmount) {
        UserCredit userCredit = getByUserEmail(userEmail, companyId);
        
        if (userCredit == null) {
             // Create new entry if it doesn't exist
             userCredit = new UserCredit();
             userCredit.setUserEmail(userEmail);
             userCredit.setCompanyId(companyId);
             userCredit.setCreditsLeft(0);
        }
        
        int newCredits = userCredit.getCreditsLeft() + changeAmount;
        
        userCredit.setCreditsLeft(newCredits);
        return userCreditRepository.save(userCredit);
    }
    
    // Helper methods
    @Override
    public UserCredit getByUserEmail(String userEmail, String companyId) {
    	
		User user = userRepo.findByEmail(userEmail, companyId);
		
		if(user==null) {
			throw new RuntimeException("User does not exists");
		}
    	
        return userCreditRepository.findByUserEmailAndCompanyId(userEmail, companyId);
    }
    
    @Override
    public void deleteById(Long id) {
        userCreditRepository.deleteById(id);
    }
    
    @Override
    public Optional<UserCredit> findById(Long id) {
        return userCreditRepository.findById(id);
    }

}

package com.letswork.crm.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Credit;
import com.letswork.crm.repo.CreditRepository;
import com.letswork.crm.service.CreditService;

@Service
@Transactional
public class CreditServiceImpl implements CreditService{
	
	@Autowired
    private CreditRepository creditRepository;
	
	@Override
    public Credit saveOrUpdate(Credit credit) {
        if (credit.getId() != null) {
            Optional<Credit> existingCreditOpt = creditRepository.findById(credit.getId());
            if (existingCreditOpt.isPresent()) {
                // Update existing record attributes
                Credit existingCredit = existingCreditOpt.get();
                
                existingCredit.setItemUnit(credit.getItemUnit());
                existingCredit.setItemQuantity(credit.getItemQuantity());
                existingCredit.setNumberOfCredits(credit.getNumberOfCredits());
                existingCredit.setPricePerCredit(credit.getPricePerCredit());
                existingCredit.setCurrency(credit.getCurrency());
                existingCredit.setCreditName(credit.getCreditName());
                // Assuming Base class attributes like companyId, etc., are handled by the framework or controller
                
                return creditRepository.save(existingCredit);
            }
        }
        // If ID is null or record not found, save as new (or save if it's an update with a new object)
        return creditRepository.save(credit);
    }

    @Override
    public PaginatedResponseDto listAll(String companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Credit> creditsPage = creditRepository.findByCompanyId(companyId, pageable);
        
        PaginatedResponseDto response = new PaginatedResponseDto();
        
        response.setList(creditsPage.getContent());
        response.setSelectedPage(creditsPage.getNumber());
        response.setTotalNumberOfRecords((int) creditsPage.getTotalElements());
        response.setTotalNumberOfPages(creditsPage.getTotalPages());
        response.setRecordsFrom(page * size);
        response.setRecordsTo(Math.min((page * size) + size, (int) creditsPage.getTotalElements()));
        
        return response;
    }
    
    public Optional<Credit> findById(Long id) {
        return creditRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        creditRepository.deleteById(id);
    }

}

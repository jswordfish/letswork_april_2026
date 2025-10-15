package com.letswork.crm.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.CreditConferenceRoomMapping;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.repo.CreditConferenceRoomMappingRepository;
import com.letswork.crm.service.CreditConferenceRoomMappingService;

@Service
@Transactional
public class CreditConferenceRoomMappingServiceImpl implements CreditConferenceRoomMappingService{
	
	@Autowired
    private CreditConferenceRoomMappingRepository mappingRepository;
	
	@Autowired
	ConferenceRoomRepository conferenceRoomRepository;

    @Override
    public CreditConferenceRoomMapping saveOrUpdate(CreditConferenceRoomMapping mapping) {
    	
    	ConferenceRoom room = conferenceRoomRepository.findByNameAndLetsWorkCentreAndCompanyId(mapping.getConferenceRoomName(), mapping.getLetsWorkCentre(), mapping.getCompanyId());
    	
    	if(room==null) {
    		throw new RuntimeException("Conference room does not exists");
    	}
    	
        if (mapping.getId() != null) {
            Optional<CreditConferenceRoomMapping> existingMappingOpt = mappingRepository.findById(mapping.getId());
            if (existingMappingOpt.isPresent()) {
                CreditConferenceRoomMapping existingMapping = existingMappingOpt.get();
                
                // Manually update attributes to avoid changing Base properties inadvertently
                existingMapping.setConferenceRoomName(mapping.getConferenceRoomName());
                existingMapping.setLetsWorkCentre(mapping.getLetsWorkCentre());
                existingMapping.setPriceFor30Mins(mapping.getPriceFor30Mins());
                existingMapping.setPriceFor1Hr(mapping.getPriceFor1Hr());
                existingMapping.setPriceFor2Hrs(mapping.getPriceFor2Hrs());
                existingMapping.setPriceFor4Hrs(mapping.getPriceFor4Hrs());
                
                return mappingRepository.save(existingMapping);
            }
        }
        return mappingRepository.save(mapping);
    }

    @Override
    public PaginatedResponseDto listAll(String companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CreditConferenceRoomMapping> mappingsPage = mappingRepository.findByCompanyId(companyId, pageable);
        
        PaginatedResponseDto response = new PaginatedResponseDto();
        
        response.setList(mappingsPage.getContent());
        response.setSelectedPage(mappingsPage.getNumber());
        response.setTotalNumberOfRecords((int) mappingsPage.getTotalElements());
        response.setTotalNumberOfPages(mappingsPage.getTotalPages());
        response.setRecordsFrom(page * size);
        response.setRecordsTo(Math.min((page * size) + size, (int) mappingsPage.getTotalElements()));
        
        return response;
    }

    @Override
    public void deleteById(Long id) {
        mappingRepository.deleteById(id);
    }
    
    @Override
    public Optional<CreditConferenceRoomMapping> findById(Long id) {
        return mappingRepository.findById(id);
    }

}

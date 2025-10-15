package com.letswork.crm.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.entities.User;
import com.letswork.crm.entities.UserSeatMapping;
import com.letswork.crm.repo.SeatRepository;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.repo.UserSeatMappingRepository;
import com.letswork.crm.service.UserSeatMappingService;

@Service
@Transactional
public class UserSeatMappingServiceImpl implements UserSeatMappingService {

    @Autowired
    private UserSeatMappingRepository userSeatMappingRepository;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private SeatRepository seatRepo;

    @Override
    public UserSeatMapping saveOrUpdate(UserSeatMapping mapping) {
    	
    	User user = userRepo.findByEmail(mapping.getEmail(), mapping.getCompanyId());
    	
    	if(user==null) {
    		
    		throw new RuntimeException("User does not exists");
    		
    	}
    	
    	Optional<Seat> seat = seatRepo.findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumber(mapping.getSeatType(), mapping.getCompanyId(), mapping.getLetsWorkCentre(), mapping.getSeatNumber());
    	
    	if(seat.isEmpty()) {
    		throw new RuntimeException("Seat does not exists");
    	}
    	
        Optional<UserSeatMapping> existingMappingOpt =
                userSeatMappingRepository.findByEmailAndCompanyIdAndLetsWorkCentre(
                        mapping.getEmail(), mapping.getCompanyId(), mapping.getLetsWorkCentre());

        if (existingMappingOpt.isPresent()) {
            UserSeatMapping existing = existingMappingOpt.get();

            existing.setSeatType(mapping.getSeatType());
            existing.setSeatNumber(mapping.getSeatNumber());
            existing.setNumberOfDays(mapping.getNumberOfDays());

            return userSeatMappingRepository.save(existing);
        } else {
            
            return userSeatMappingRepository.save(mapping);
        }
    }

    @Override
    public PaginatedResponseDto listMappings(String companyId, String letsWorkCentre, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("id").descending());
        Page<UserSeatMapping> page = userSeatMappingRepository.findByCompanyIdAndLetsWorkCentre(companyId, letsWorkCentre, pageable);

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((pageNo - 1) * pageSize + 1);
        response.setRecordsTo((int) Math.min(pageNo * pageSize, page.getTotalElements()));
        response.setTotalNumberOfRecords((int) page.getTotalElements());
        response.setTotalNumberOfPages(page.getTotalPages());
        response.setSelectedPage(pageNo);
        response.setList(page.getContent());

        return response;
    }

    @Override
    public void deleteMapping(Long id) {
        userSeatMappingRepository.deleteById(id);
    }
}

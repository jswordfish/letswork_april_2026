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
import com.letswork.crm.entities.Location;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.enums.SeatType;
import com.letswork.crm.repo.LocationRepository;
import com.letswork.crm.repo.SeatRepository;
import com.letswork.crm.repo.UserSeatMappingRepository;
import com.letswork.crm.service.SeatService;

@Service
@Transactional
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private LocationRepository locationRepo;
    
    @Autowired
    private UserSeatMappingRepository userSeatMappingRepository;

    @Override
    public Seat saveOrUpdate(Seat seat) {
    	
    	Location loc = locationRepo.findByNameAndCompanyId(seat.getLocation(), seat.getCompanyId());
    	
    	if(loc==null) {
    		throw new RuntimeException("This location does not exists");
    	}
    	
        Optional<Seat> existingSeatOpt = seatRepository.findBySeatTypeAndCompanyIdAndLocationAndSeatNumber(seat.getSeatType(), seat.getCompanyId(), seat.getLocation(), seat.getSeatNumber());

        if (existingSeatOpt.isPresent()) {
            Seat existingSeat = existingSeatOpt.get();

            
            existingSeat.setCostPerDay(seat.getCostPerDay());
            existingSeat.setCostPerMonth(seat.getCostPerMonth());

            return seatRepository.save(existingSeat);
        } else {
            
            return seatRepository.save(seat);
        }
    }

    @Override
    public PaginatedResponseDto listSeats(String companyId, String location, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("id").descending());
        Page<Seat> page = seatRepository.findByCompanyIdAndLocation(companyId, location, pageable);

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
    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }
    
    
    @Override
    public long getTotalSeats(String companyId, String location, SeatType seatType) {
        return seatRepository.countByCompanyIdAndLocationAndSeatType(companyId, location, seatType);
    }

    @Override
    public long getAvailableSeats(String companyId, String location, SeatType seatType) {
        long totalSeats = seatRepository.countByCompanyIdAndLocationAndSeatType(companyId, location, seatType);
        long occupiedSeats = userSeatMappingRepository.countByCompanyIdAndLocationAndSeatType(companyId, location, seatType);
        return Math.max(totalSeats - occupiedSeats, 0);
    }
    
}

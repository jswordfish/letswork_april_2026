package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.SeatExcelDto;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.SeatType;
import com.letswork.crm.repo.CabinRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.SeatRepository;
import com.letswork.crm.repo.UserSeatMappingRepository;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.SeatService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;


@Service
@Transactional
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private LetsWorkCentreRepository letsWorkCentreRepo;
    
    @Autowired
    private UserSeatMappingRepository userSeatMappingRepository;
    
    @Autowired
    private TenantService tenantService;
    
    @Autowired
    private CabinRepository cabinRepository;
    
    @Autowired
	LetsWorkCentreService letsWorkCentreService;
    
    ModelMapper mapper = new ModelMapper();

    @Override
    public Seat saveOrUpdate(Seat seat) {
        
        Tenant tenant = tenantService.findTenantByCompanyId(seat.getCompanyId());
        
        if(tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + seat.getCompanyId());
        }
        
        LetsWorkCentre loc = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(seat.getLetsWorkCentre(), seat.getCompanyId(), seat.getCity(), seat.getState());
        
        if(loc == null) {
            throw new RuntimeException("This letsWorkCentre does not exists");
        }
        
        // This is the correct logic for SHARED_CABIN seats
        if (seat.getSeatType() == SeatType.SHARED_CABIN) {
            if (!StringUtils.hasText(seat.getCabinName())) {
                throw new RuntimeException("Cabin name is required for SHARED_CABIN seat type");
            }

            boolean cabinExists = cabinRepository.existsByCabinNameAndCompanyIdAndLetsWorkCentreAndCityAndState(
                    seat.getCabinName(), seat.getCompanyId(), seat.getLetsWorkCentre(), seat.getCity(), seat.getState());
            if (!cabinExists) {
                throw new RuntimeException("Cabin does not exist: " + seat.getCabinName());
            }
        } else {
            // Enforce null cabin name for other types at the persistence layer
            seat.setCabinName(null);
        }
        
        
        Optional<Seat> existingSeatOpt = seatRepository.findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumberAndCityAndState(seat.getSeatType(), seat.getCompanyId(), seat.getLetsWorkCentre(), seat.getSeatNumber(), seat.getCity(), seat.getState());

        if (existingSeatOpt.isPresent()) {
            Seat existingSeat = existingSeatOpt.get();
            seat.setCreateDate(existingSeat.getCreateDate());
            seat.setUpdateDate(new Date());
            seat.setId(existingSeat.getId());
            mapper.map(seat, existingSeat);
            return seatRepository.save(existingSeat);
        } else {
            seat.setCreateDate(new Date());
            
            return seatRepository.save(seat);
        }
    }
    
    // validate Method (Modified to enforce null cabinName for non-SHARED_CABIN) 
    
    private String validate(SeatExcelDto dto) {
        if(dto.getSeatType() == null || dto.getSeatType().length() == 0) {
            return "Seat Type Should not be null";
        }
        
        if(dto.getSeatNumber() == null || dto.getSeatNumber().length() == 0) {
            return "Seat Number Should not be null";        
            }
        
        if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
            return "LetsWork Centre Should not be null";    
            }
        
        if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
            return "CompanyId Should not be null";    
            }
        
        if(dto.getCostPerDay() == null) {
            return "Cost Per Day Should not be null";    
            }
        
        if(dto.getCostPerMonth() == null) {
            return "Cost Per Month Should not be null";    
            }
        
        if(dto.getCity() == null || dto.getCity().length() == 0) {
			return "City Should not be null";	
			}
		
		if(dto.getState() == null || dto.getState().length() == 0) {
			return "State Should not be null";	
			}
        
        if(tenantService.findTenantByCompanyId(dto.getCompanyId()) == null) {
            return "CompanyId "+dto.getCompanyId()+" does not exists";
        }
        
        if(letsWorkCentreService.findByName(dto.getLetsWorkCentre(), dto.getCompanyId(), dto.getCity(), dto.getState()) == null){
			return "Letswork Cente "+dto.getLetsWorkCentre()+" does not exist";
		}
        
        
        if ("SHARED_CABIN".equalsIgnoreCase(dto.getSeatType())) {
            if (dto.getCabinName() == null || dto.getCabinName().trim().isEmpty()) {
                return "Cabin name must exist when seat type is SHARED_CABIN";
            }
            
            boolean cabinExists = cabinRepository.existsByCabinNameAndCompanyIdAndLetsWorkCentreAndCityAndState(
                        dto.getCabinName(), dto.getCompanyId(), dto.getLetsWorkCentre(), dto.getCity(), dto.getState());

            if (!cabinExists) {
                return "Cabin with name " + dto.getCabinName() + " does not exist for the given company and location";
            }
            
        } else if (dto.getCabinName() != null && !dto.getCabinName().trim().isEmpty()) {
            
            return "Cabin name must be empty for seat type: " + dto.getSeatType();
        }
        
        
        return "ok";
    }
    
    
    @Override
    public String uploadSeatExcel(MultipartFile file) throws IOException {
        List<SeatExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, SeatExcelDto.class);
        
        
        for(SeatExcelDto dto : dtos) {
            String val = validate(dto);
            if(!val.equalsIgnoreCase("ok")) {
                return val;
            }
        }
        

        
        for (SeatExcelDto dto : dtos) {
            try {
                
                String cabinName = null;
                if (dto.getCabinName() != null) {
                    
                    cabinName = dto.getCabinName().trim();
                }
                
                Seat seat = Seat.builder()
                        .companyId(dto.getCompanyId().trim())
                        .letsWorkCentre(dto.getLetsWorkCentre().trim())
                        .seatType(SeatType.valueOf(dto.getSeatType().toUpperCase()))
                        .seatNumber(dto.getSeatNumber().trim())
                        .costPerDay(dto.getCostPerDay() == null ? 0 : dto.getCostPerDay())
                        .costPerMonth(dto.getCostPerMonth() == null ? 0 : dto.getCostPerMonth())
                        .cabinName(cabinName) 
                        .city(dto.getCity().trim())
                        .state(dto.getState().trim())
                        .build();

                saveOrUpdate(seat);
            } catch (Exception e) {
               
                return "problem " + e.getMessage();
            }
            
        }

        return "ok";
    }


    @Override
    public PaginatedResponseDto listSeats(String companyId, String letsWorkCentre, String city, String state, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("id").descending());
        Page<Seat> page = seatRepository.findByCompanyIdAndLetsWorkCentreAndCityAndState(companyId, letsWorkCentre, city, state, pageable);

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
    public long getTotalSeats(String companyId, String letsWorkCentre, SeatType seatType, String city, String state) {
        return seatRepository.countByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(companyId, letsWorkCentre, seatType, city, state);
    }

    @Override
    public long getAvailableSeats(String companyId, String letsWorkCentre, SeatType seatType, String city, String state) {
        long totalSeats = seatRepository.countByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(companyId, letsWorkCentre, seatType, city, state);
        long occupiedSeats = userSeatMappingRepository.countByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(companyId, letsWorkCentre, seatType, city, state);
        return Math.max(totalSeats - occupiedSeats, 0);
    }
    
    
    @Override
    public PaginatedResponseDto findByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page) {
        // Check if letsWorkCentre exists
    	LetsWorkCentre loc = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state);
        if (loc == null) {
            return new PaginatedResponseDto(); 
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        Page<Seat> seatPage = seatRepository.findByLetsWorkCentreAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state, pageable);

        return buildPaginatedResponse(seatPage, page);
    }
    
    
    private static final int PAGE_SIZE = 10;
    
    private PaginatedResponseDto buildPaginatedResponse(Page<Seat> seatPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) seatPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) seatPage.getTotalElements());
        response.setTotalNumberOfPages(seatPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(seatPage.getContent());
        return response;
    }
    
}

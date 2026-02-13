package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.SeatAvailabilityDto;
import com.letswork.crm.dtos.SeatExcelDto;
import com.letswork.crm.dtos.SeatMappingResponseDto;
import com.letswork.crm.dtos.SeatPublishRequest;
import com.letswork.crm.dtos.SeatPublishResponse;
import com.letswork.crm.entities.Cabin;
import com.letswork.crm.entities.ClientCompanySeatMapping;
import com.letswork.crm.entities.ContractSeatMapping;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.entities.SeatKey;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.entities.UserSeatMapping;
import com.letswork.crm.enums.SeatType;
import com.letswork.crm.repo.CabinRepository;
import com.letswork.crm.repo.ClientCompanySeatMappingRepository;
import com.letswork.crm.repo.ContractRepository;
import com.letswork.crm.repo.ContractSeatMappingRepository;
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
    
    @Autowired
    ClientCompanySeatMappingRepository clientCompanySeatMappingRepository;
    
    @Autowired
    ContractSeatMappingRepository contractSeatMappingRepository;
    
    @Autowired
    ContractRepository contractRepository;
    
    ModelMapper mapper = new ModelMapper();

    @Override
    public Seat saveOrUpdate(Seat seat) {

        Tenant tenant = tenantService.findTenantByCompanyId(seat.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + seat.getCompanyId());
        }

        LetsWorkCentre loc = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                seat.getLetsWorkCentre(), seat.getCompanyId(), seat.getCity(), seat.getState());

        if (loc == null) {
            throw new RuntimeException("This letsWorkCentre does not exist");
        }

        // ✅ Cabin-related validation
        if (seat.getSeatType() == SeatType.SHARED_CABIN || seat.getSeatType() == SeatType.CABIN_DESK) {
            if (!StringUtils.hasText(seat.getCabinName())) {
                throw new RuntimeException("Cabin name is required for SHARED_CABIN or CABIN_DESK seat type");
            }

            Cabin cabin = cabinRepository.findByCabinNameAndCompanyIdAndLetsWorkCentreAndCityAndState(
                    seat.getCabinName(), seat.getCompanyId(), seat.getLetsWorkCentre(),
                    seat.getCity(), seat.getState());

            if (cabin == null) {
                throw new RuntimeException("Cabin does not exist: " + seat.getCabinName());
            }

            // ✅ Check current seat count in cabin
            long currentSeatCount = seatRepository.countByCabinNameAndCompanyIdAndLetsWorkCentreAndCityAndState(
                    seat.getCabinName(), seat.getCompanyId(), seat.getLetsWorkCentre(),
                    seat.getCity(), seat.getState());

            if (currentSeatCount > cabin.getTotalSeats()) {
                throw new RuntimeException("Cabin " + seat.getCabinName() + " is full. Cannot add more seats.");
            }
        } else {
            seat.setCabinName(null);
        }

        // ✅ Check for duplicate seat (same seatType, seatNumber, etc.)
        Optional<Seat> existingSeatOpt = seatRepository.findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumberAndCityAndState(
                seat.getSeatType(), seat.getCompanyId(), seat.getLetsWorkCentre(),
                seat.getSeatNumber(), seat.getCity(), seat.getState());

        if (existingSeatOpt.isPresent()) {
            Seat existingSeat = existingSeatOpt.get();
            seat.setCreateDate(existingSeat.getCreateDate());
            seat.setUpdateDate(new Date());
            seat.setId(existingSeat.getId());
            mapper.map(seat, existingSeat);
            return seatRepository.save(existingSeat);
        } else {
            seat.setCreateDate(new Date());
            seat.setPublished(false);
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
        
        
        if ("SHARED_CABIN".equalsIgnoreCase(dto.getSeatType()) || "CABIN_DESK".equalsIgnoreCase(dto.getSeatType())) {
            if (dto.getCabinName() == null || dto.getCabinName().trim().isEmpty()) {
                return "Cabin name must exist when seat type is SHARED_CABIN or CABIN_DESK";
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
    public PaginatedResponseDto listSeats(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            SeatType seatType,
            String search,
            String sort,
            int pageNo,
            int pageSize) {

        Pageable pageable = buildPageable(sort, pageNo, pageSize);

        Page<Seat> page = seatRepository.findWithFilters(
                companyId,
                emptyToNull(letsWorkCentre),
                emptyToNull(city),
                emptyToNull(state),
                seatType,
                emptyToNull(search),
                pageable
        );

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom(pageNo * pageSize + 1);
        response.setRecordsTo((int) Math.min((pageNo + 1) * pageSize, page.getTotalElements()));
        response.setTotalNumberOfRecords((int) page.getTotalElements());
        response.setTotalNumberOfPages(page.getTotalPages());
        response.setSelectedPage(pageNo);
        response.setList(page.getContent());

        return response;
    }
    
    private Pageable buildPageable(String sort, int pageNo, int pageSize) {

        String sortField = "id";
        Sort.Direction direction = Sort.Direction.DESC;

        if (sort != null && sort.contains("=")) {
            String[] parts = sort.split("=", 2);
            sortField = parts[0].trim();
            direction = parts[1].equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
        }

        return PageRequest.of(pageNo, pageSize, Sort.by(direction, sortField));
    }
    
    private String emptyToNull(String val) {
        return (val == null || val.trim().isEmpty()) ? null : val;
    }
    
    @Override
    public PaginatedResponseDto listPublishedSeats(String companyId, String letsWorkCentre, String city, String state, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by("id").descending());
        Page<Seat> page = seatRepository.findPublishedSeatsByLetsWorkCentreAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state, pageable);

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
    public List<SeatAvailabilityDto> getAllSeatsWithAvailability(
            String companyId, String letsWorkCentre, String city, String state) {

        List<Seat> allSeats =
                seatRepository.findAllByCompanyIdAndLetsWorkCentreAndCityAndState(
                        companyId, letsWorkCentre, city, state);

        // 🔥 Fetch active contract seat mappings in one query
        List<ContractSeatMapping> activeContractMappings =
                contractSeatMappingRepository.findActiveByLocation(
                        companyId, letsWorkCentre, city, state);

        // 🔥 Build lookup map: SeatKey -> ContractSeatMapping
        Map<SeatKey, ContractSeatMapping> contractSeatMap =
                activeContractMappings.stream()
                        .collect(Collectors.toMap(
                                c -> new SeatKey(
                                        c.getLetsWorkCentre(),
                                        c.getCity(),
                                        c.getState(),
                                        c.getCompanyId(),
                                        c.getSeatType(),
                                        c.getSeatNumber()
                                ),
                                c -> c,
                                (a, b) -> a
                        ));

        return allSeats.stream()
                .map(seat -> {

                    SeatKey key = new SeatKey(
                            seat.getLetsWorkCentre(),
                            seat.getCity(),
                            seat.getState(),
                            seat.getCompanyId(),
                            seat.getSeatType(),
                            seat.getSeatNumber()
                    );

                    ContractSeatMapping mapping = contractSeatMap.get(key);
                    boolean available = (mapping == null);

                    SeatAvailabilityDto dto = new SeatAvailabilityDto();
                    dto.setSeat(seat);
                    dto.setAvailable(available);

                    if (!available) {

                        dto.setContractId(mapping.getContractId());
                        dto.setContractStartDate(mapping.getStartDate());
                        dto.setContractEndDate(
                                mapping.getActualEndDate() != null
                                        ? mapping.getActualEndDate()
                                        : mapping.getEndDate()
                        );

                        // 🔥 Fetch Contract → LetsWorkClient → company name
                        contractRepository
                                .findByIdAndCompanyId(mapping.getContractId(), companyId)
                                .ifPresent(contract -> {
                                    LetsWorkClient client = contract.getLetsWorkClient();
                                    dto.setCompanyName(client.getClientCompanyName());
                                });
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    
    @Override
    public long getTotalSeats(String companyId, String letsWorkCentre, SeatType seatType, String city, String state) {
        return seatRepository.countByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(companyId, letsWorkCentre, seatType, city, state);
    }

//    @Override
//    public long getAvailableSeats(String companyId, String letsWorkCentre, SeatType seatType, String city, String state) {
//        long totalSeats = seatRepository.countByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(companyId, letsWorkCentre, seatType, city, state);
//        long occupiedSeats = userSeatMappingRepository.countByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(companyId, letsWorkCentre, seatType, city, state);
//        return Math.max(totalSeats - occupiedSeats, 0);
//    }
    
    @Override
    public PaginatedResponseDto getAvailableSeats(String companyId, String letsWorkCentre, SeatType seatType, String city, String state, int page) {

        
        List<Seat> allSeats = seatRepository.findAllByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(
                companyId, letsWorkCentre, seatType, city, state);

        
        List<String> userOccupiedSeats = userSeatMappingRepository
                .findSeatNumbersByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(companyId, letsWorkCentre, seatType, city, state);

        
        List<String> companyOccupiedSeats = clientCompanySeatMappingRepository
                .findSeatNumbersByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(companyId, letsWorkCentre, seatType, city, state);

        
        Set<String> occupiedSeatNumbers = new HashSet<>();
        occupiedSeatNumbers.addAll(userOccupiedSeats);
        occupiedSeatNumbers.addAll(companyOccupiedSeats);

        
        List<Seat> availableSeats = allSeats.stream()
                .filter(seat -> !occupiedSeatNumbers.contains(seat.getSeatNumber()))
                .collect(Collectors.toList());

        
        int pageSize = 10; 
        int start = Math.min(page * pageSize, availableSeats.size());
        int end = Math.min(start + pageSize, availableSeats.size());

        List<Seat> paginatedSeats = availableSeats.subList(start, end);
        Page<Seat> seatPage = new PageImpl<>(paginatedSeats, PageRequest.of(page, pageSize), availableSeats.size());

        
        return buildPaginatedResponse(seatPage, page);
    }
    
    
    @Override
    public PaginatedResponseDto findByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page) {
        // Check if letsWorkCentre exists
    	
		Tenant tenant = tenantService.findTenantByCompanyId(companyId);
        
        if(tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + companyId);
        }
        
        LetsWorkCentre loc = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state);
        
        if(loc == null) {
            throw new RuntimeException("This letsWorkCentre does not exists");
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("seatNumber").ascending());
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

	@Override
	public SeatPublishResponse publishSeats(List<SeatPublishRequest> requests) {

	    SeatPublishResponse response = new SeatPublishResponse();

	    for (SeatPublishRequest req : requests) {

	        Optional<Seat> existingSeatOpt =
	                seatRepository.findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumberAndCityAndState(
	                        req.getSeatType(),
	                        req.getCompanyId(),
	                        req.getLetsWorkCentre(),
	                        req.getSeatNumber(),
	                        req.getCity(),
	                        req.getState()
	                );

	        if (existingSeatOpt.isPresent()) {

	            Seat seat = existingSeatOpt.get();

	            if (Boolean.TRUE.equals(seat.getPublished())) {
	                response.getAlreadyPublished().add(
	                        seat.getSeatNumber() + " already published"
	                );
	                continue;
	            }

	            seat.setPublished(true);
	            seat.setUpdateDate(new Date());
	            saveOrUpdate(seat);

	            response.getPublished().add(
	                    seat.getSeatNumber() + " published successfully"
	            );
	        } else {
	            response.getNotFound().add(
	                    req.getSeatNumber() + " not found"
	            );
	        }
	    }

	    return response;
		
	}
	
	@Override
	public Page<SeatMappingResponseDto> getAllSeatMappings(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            int page,
            int size) {

        List<SeatMappingResponseDto> combinedList = new ArrayList<>();

        
        List<UserSeatMapping> users = userSeatMappingRepository.findByCompanyId(companyId);

        
        List<ClientCompanySeatMapping> clients = clientCompanySeatMappingRepository.findByCompanyId(companyId);

        
        List<SeatMappingResponseDto> userDtos = users.stream()
                .map(user -> new SeatMappingResponseDto(
                        user.getId(),
                        user.getEmail(),
                        user.getLetsWorkCentre(),
                        user.getSeatType(),
                        user.getSeatNumber(),
                        user.getStartDate(),
                        user.getEndDate(),
                        user.getCity(),
                        user.getState(),
                        "USER"
                ))
                .collect(Collectors.toList());

        List<SeatMappingResponseDto> clientDtos = clients.stream()
                .map(client -> new SeatMappingResponseDto(
                        client.getId(),
                        client.getClientCompanyName(),
                        client.getLetsWorkCentre(),
                        client.getSeatType(),
                        client.getSeatNumber(),
                        client.getStartDate(),
                        client.getEndDate(),
                        client.getCity(),
                        client.getState(),
                        "CLIENT_COMPANY"
                ))
                .collect(Collectors.toList());

        combinedList.addAll(userDtos);
        combinedList.addAll(clientDtos);

        
        if (letsWorkCentre != null && !letsWorkCentre.isBlank()) {
            combinedList = combinedList.stream()
                    .filter(m -> letsWorkCentre.equalsIgnoreCase(m.getLetsWorkCentre()))
                    .collect(Collectors.toList());
        }

        if (city != null && !city.isBlank()) {
            combinedList = combinedList.stream()
                    .filter(m -> city.equalsIgnoreCase(m.getCity()))
                    .collect(Collectors.toList());
        }

        if (state != null && !state.isBlank()) {
            combinedList = combinedList.stream()
                    .filter(m -> state.equalsIgnoreCase(m.getState()))
                    .collect(Collectors.toList());
        }

        
        combinedList = combinedList.stream()
                .sorted(Comparator.comparing(SeatMappingResponseDto::getStartDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        
        int start = (int) PageRequest.of(page, size).getOffset();
        int end = Math.min(start + size, combinedList.size());
        List<SeatMappingResponseDto> paginatedList = combinedList.subList(start, end);

        return new PageImpl<>(paginatedList, PageRequest.of(page, size), combinedList.size());
    }
	
	@Override
	public List<Seat> listSeatsInCabin(String companyId, String letsWorkCentre, String city, String state, String cabinName) {
	    return seatRepository.findByCabinDetails(companyId, letsWorkCentre, city, state, cabinName);
	}
    
}

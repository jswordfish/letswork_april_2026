package com.letswork.crm.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.BulkSeatAssignmentRequestContract;
import com.letswork.crm.dtos.SeatAssignmentDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.entities.ContractSeatMapping;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Seat;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.ContractRepository;
import com.letswork.crm.repo.ContractSeatMappingRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.SeatRepository;
import com.letswork.crm.service.ContractSeatMappingService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class ContractSeatMappingServiceImpl implements ContractSeatMappingService{
	
	@Autowired
	ContractSeatMappingRepository repo;
	
	@Autowired
	ContractRepository contractRepo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	@Autowired
    SeatRepository seatRepo;

	
	@Override
	public ContractSeatMapping saveOrUpdate(ContractSeatMapping mapping) {

	    Tenant tenant =
	            tenantService.findTenantByCompanyId(
	                    mapping.getCompanyId()
	            );

	    if (tenant == null) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Invalid CompanyId - " + mapping.getCompanyId()
	        );
	    }

	    Contract contract =
	            contractRepo
	                    .findByIdAndCompanyId(
	                            mapping.getContract().getId(),
	                            mapping.getCompanyId()
	                    )
	                    .orElseThrow(() ->
	                            new ResponseStatusException(
	                                    HttpStatus.BAD_REQUEST,
	                                    "Invalid Contract"
	                            )
	                    );

	    LetsWorkCentre centre =
	            letsWorkCentreRepo
	                    .findByNameAndCompanyIdAndCityAndState(
	                            mapping.getContract().getLetsWorkCentre().getName(),
	                            mapping.getCompanyId(),
	                            mapping.getContract().getLetsWorkCentre().getCity(),
	                            mapping.getContract().getLetsWorkCentre().getState()
	                    );

	    if (centre == null) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Invalid LetsWorkCentre"
	        );
	    }

	    Optional<Seat> seat =
	            seatRepo
	                    .findBySeatTypeAndCompanyIdAndLetsWorkCentreAndSeatNumberAndCityAndStateAndPublishedTrue(
	                            mapping.getSeat().getSeatType(),
	                            mapping.getCompanyId(),
	                            mapping.getContract().getLetsWorkCentre().getName(),
	                            mapping.getSeat().getSeatNumber(),
	                            mapping.getContract().getLetsWorkCentre().getCity(),
	                            mapping.getContract().getLetsWorkCentre().getState()
	                    );

	    if (seat.isEmpty()) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Seat does not exist or not published"
	        );
	    }

	    // IMPORTANT:
	    // Replace temporary objects with managed entities from DB
	    mapping.setContract(contract);
	    mapping.setSeat(seat.get());

	    Optional<ContractSeatMapping> alreadyAssigned =
	            repo.findBySeatNumberAndSeatTypeAndLetsWorkCentreAndCompanyIdAndCityAndState(
	                    seat.get().getSeatNumber(),
	                    seat.get().getSeatType(),
	                    centre.getName(),
	                    mapping.getCompanyId(),
	                    centre.getCity(),
	                    centre.getState()
	            );

	    if (alreadyAssigned.isPresent()) {

	        if (!alreadyAssigned.get()
	                .getContract()
	                .getId()
	                .equals(contract.getId())) {

	            throw new ResponseStatusException(
	                    HttpStatus.BAD_REQUEST,
	                    "Seat " + seat.get().getSeatNumber()
	                            + " is already assigned to another contract"
	            );
	        }
	    }

	    Optional<ContractSeatMapping> existingOpt =
	            repo.findByFullBusinessKey(
	                    contract.getId(),
	                    centre.getName(),
	                    mapping.getCompanyId(),
	                    centre.getCity(),
	                    centre.getState(),
	                    seat.get().getSeatType(),
	                    seat.get().getSeatNumber()
	            );

	    ModelMapper mapper = new ModelMapper();

	    if (existingOpt.isPresent()) {

	        ContractSeatMapping existing =
	                existingOpt.get();

	        mapping.setId(existing.getId());
	        mapping.setUpdateDate(new Date());

	        mapper.map(mapping, existing);

	        return repo.save(existing);

	    } else {

	        mapping.setCreateDate(new Date());

	        return repo.save(mapping);
	    }
	}

	@Override
	public List<ContractSeatMapping> assignMultipleSeatsToContract(
	        BulkSeatAssignmentRequestContract request
	) {

	    List<ContractSeatMapping> savedMappings = new ArrayList<>();

	    for (SeatAssignmentDto seatDto : request.getSeats()) {

	        ContractSeatMapping mapping =
	                new ContractSeatMapping();

	        Contract contract =
	                new Contract();

	        contract.setId(
	                request.getContractId()
	        );

	        LetsWorkCentre centre =
	                new LetsWorkCentre();

	        centre.setName(
	                request.getLetsWorkCentre()
	        );

	        centre.setCity(
	                request.getCity()
	        );

	        centre.setState(
	                request.getState()
	        );

	        contract.setLetsWorkCentre(
	                centre
	        );

	        Seat seat =
	                new Seat();

	        seat.setSeatType(
	                seatDto.getSeatType()
	        );

	        seat.setSeatNumber(
	                seatDto.getSeatNumber()
	        );

	        mapping.setContract(
	                contract
	        );

	        mapping.setSeat(
	                seat
	        );

	        mapping.setCompanyId(
	                request.getCompanyId()
	        );

	        mapping.setStartDate(
	                request.getStartDate()
	        );

	        mapping.setEndDate(
	                request.getEndDate()
	        );

	        ContractSeatMapping saved =
	                this.saveOrUpdate(
	                        mapping
	                );

	        savedMappings.add(
	                saved
	        );
	    }

	    return savedMappings;
	}

	@Override
	public List<ContractSeatMapping> getSeatsByContract(Long contractId, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByContractIdAndCompanyId(contractId, companyId);
	}

}
